package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.order.OrderApi;
import ru.yandex.practicum.api.shoppingStore.ShoppingStoreApi;
import ru.yandex.practicum.DTO.order.OrderDto;
import ru.yandex.practicum.DTO.payment.PaymentDto;
import ru.yandex.practicum.DTO.shoppingStore.ProductDto;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.enums.payment.PaymentStatus;
import ru.yandex.practicum.exception.payment.NoPaymentFoundException;
import ru.yandex.practicum.exception.payment.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.10");

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreApi shoppingStoreApi;
    private final OrderApi orderApi;

    public BigDecimal productCost(OrderDto order) {
        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("В заказе нет товаров");
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (Map.Entry<UUID, Long> entry : order.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();
            ProductDto product = shoppingStoreApi.getProductById(productId);
            if (product == null || product.getPrice() == null) {
                throw new NotEnoughInfoInOrderToCalculateException("Нет цены для товара " + productId);
            }
            sum = sum.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }
        return sum.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalCost(OrderDto order) {
        validateOrderPrices(order);
        PaymentAmounts amounts = calculatePaymentAmounts(order.getProductPrice(), order.getDeliveryPrice());
        return amounts.totalPayment();
    }

    @Transactional
    public PaymentDto payment(OrderDto order) {
        validateOrderPrices(order);
        PaymentAmounts amounts = calculatePaymentAmounts(order.getProductPrice(), order.getDeliveryPrice());
        Payment payment = paymentMapper.toEntity(
                order.getOrderId(), order.getProductPrice(), order.getDeliveryPrice(),
                amounts.feeTotal(), amounts.totalPayment());
        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toDto(saved);
    }

    @Transactional
    public void paymentSuccess(UUID paymentId) {
        updatePaymentStatusAndNotify(paymentId, PaymentStatus.SUCCESS, orderApi::payment);
    }

    @Transactional
    public void paymentFailed(UUID paymentId) {
        updatePaymentStatusAndNotify(paymentId, PaymentStatus.FAILED, orderApi::paymentFailed);
    }

    private void validateOrderPrices(OrderDto order) {
        if (order.getProductPrice() == null || order.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Нужны стоимость товаров и доставки в заказе");
        }
    }

    private PaymentAmounts calculatePaymentAmounts(BigDecimal productPrice, BigDecimal deliveryPrice) {
        BigDecimal feeTotal = productPrice.multiply(VAT_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPayment = productPrice.add(feeTotal).add(deliveryPrice).setScale(2, RoundingMode.HALF_UP);
        return new PaymentAmounts(feeTotal, totalPayment);
    }

    private void updatePaymentStatusAndNotify(UUID paymentId, PaymentStatus status, Consumer<UUID> orderApiCallback) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoPaymentFoundException("Не найден платеж " + paymentId));
        payment.setStatus(status);
        paymentRepository.save(payment);
        orderApiCallback.accept(payment.getOrderId());
    }

    private record PaymentAmounts(BigDecimal feeTotal, BigDecimal totalPayment) {
    }
}
