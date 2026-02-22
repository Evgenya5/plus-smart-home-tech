package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.DTO.payment.PaymentDto;
import ru.yandex.practicum.api.delivery.DeliveryApi;
import ru.yandex.practicum.api.payment.PaymentApi;
import ru.yandex.practicum.api.warehouse.WarehouseApi;
import ru.yandex.practicum.DTO.delivery.DeliveryDto;
import ru.yandex.practicum.enums.delivery.DeliveryState;
import ru.yandex.practicum.DTO.order.CreateNewOrderRequest;
import ru.yandex.practicum.DTO.order.OrderDto;
import ru.yandex.practicum.enums.order.OrderState;
import ru.yandex.practicum.DTO.order.ProductReturnRequest;
import ru.yandex.practicum.DTO.warehouse.AddressDto;
import ru.yandex.practicum.DTO.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.DTO.warehouse.BookedProductsDto;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.exception.order.*;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final WarehouseApi warehouseApi;
    private final PaymentApi paymentApi;
    private final DeliveryApi deliveryApi;

    public List<OrderDto> getClientOrders(String username) {
        List<Order> orders = repository.findByUsername(username);
        return mapper.toDtoList(orders);
    }

    @Transactional
    public OrderDto createNewOrder(String username, CreateNewOrderRequest request) {
        validateCreateOrderRequest(request);
        BigDecimal productPrice = getProductCostFromPayment(request.getShoppingCart().getProducts());
        Order order = new Order();
        order.setUsername(username);
        order.setShoppingCartId(request.getShoppingCart().getShoppingCartId());
        order.setProducts(new HashMap<>(request.getShoppingCart().getProducts()));
        order.setState(OrderState.NEW);
        order.setProductPrice(productPrice);
        Order saved = repository.save(order);
        AssemblyProductsForOrderRequest assemblyRequest = AssemblyProductsForOrderRequest.builder()
                .orderId(saved.getOrderId())
                .products(saved.getProducts())
                .build();
        BookedProductsDto deliveryInfo = warehouseApi.assemblyProductsForOrder(assemblyRequest);

        if (deliveryInfo == null) {
            throw new NoSpecifiedProductInWarehouseException();
        }
        saved.setDeliveryWeight(deliveryInfo.getDeliveryWeight());
        saved.setDeliveryVolume(deliveryInfo.getDeliveryVolume());
        saved.setFragile(deliveryInfo.getFragile() != null ? deliveryInfo.getFragile() : false);
        planDeliveryForOrder(saved, request.getDeliveryAddress());
        repository.save(saved);
        return mapper.toDto(saved);
    }

    @Transactional
    public OrderDto assembly(UUID orderId) {
        return transitionOrderStateAndSave(orderId, OrderState.ASSEMBLED, List.of(OrderState.NEW));
    }

    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        return transitionOrderStateAndSave(orderId, OrderState.ASSEMBLY_FAILED,
                List.of(OrderState.NEW, OrderState.ASSEMBLED));
    }

    @Transactional
    public OrderDto payment(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        if (order.getPaymentId() == null) {
            validateStateTransition(order.getState(), OrderState.ON_PAYMENT,
                    List.of(OrderState.ASSEMBLED));
            OrderDto orderDto = mapper.toDto(order);
            PaymentDto paymentDto = paymentApi.payment(orderDto);
            if (paymentDto != null && paymentDto.getPaymentId() != null) {
                order.setPaymentId(paymentDto.getPaymentId());
                order.setState(OrderState.ON_PAYMENT);
                repository.save(order);
            }
            return mapper.toDto(order);
        }
        return transitionOrderStateAndSave(orderId, OrderState.PAID,
                List.of(OrderState.ASSEMBLED, OrderState.ON_PAYMENT));
    }

    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        return transitionOrderStateAndSave(orderId, OrderState.PAYMENT_FAILED,
                List.of(OrderState.ASSEMBLED, OrderState.ON_PAYMENT, OrderState.PAID));
    }

    @Transactional
    public OrderDto delivery(UUID orderId) {
        return transitionOrderStateAndSave(orderId, OrderState.DELIVERED,
                List.of(OrderState.PAID, OrderState.ON_DELIVERY));
    }

    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        return transitionOrderStateAndSave(orderId, OrderState.DELIVERY_FAILED,
                List.of(OrderState.PAID, OrderState.ON_DELIVERY, OrderState.DELIVERED));
    }

    @Transactional
    public OrderDto complete(UUID orderId) {
        return transitionOrderStateAndSave(orderId, OrderState.COMPLETED, List.of(OrderState.DELIVERED));
    }

    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        Order order = getOrderOrThrow(request.getOrderId());
        validateStateTransition(order.getState(), OrderState.PRODUCT_RETURNED,
                List.of(OrderState.COMPLETED, OrderState.DELIVERED));
        validateReturnedProducts(order.getProducts(), request.getProducts());
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            warehouseApi.acceptReturn(request.getProducts());
        }
        return applyOrderStateAndSave(order, OrderState.PRODUCT_RETURNED);
    }

    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        validateStateTransition(order.getState(), OrderState.NEW,
                List.of(OrderState.NEW, OrderState.ASSEMBLED));
        BookedProductsDto deliveryInfo;
        try {
            AssemblyProductsForOrderRequest assemblyRequest = AssemblyProductsForOrderRequest.builder()
                    .orderId(order.getOrderId())
                    .products(order.getProducts())
                    .build();
            BookedProductsDto bookedProducts = warehouseApi.assemblyProductsForOrder(assemblyRequest);

            if (bookedProducts == null) {
                throw new NoSpecifiedProductInWarehouseException();
            }
            deliveryInfo = bookedProducts;
        } catch (Exception e) {
            throw new DeliveryCalculationException("Не удалось получить характеристики доставки для заказа " + orderId);
        }
        order.setDeliveryWeight(deliveryInfo.getDeliveryWeight());
        order.setDeliveryVolume(deliveryInfo.getDeliveryVolume());
        order.setFragile(deliveryInfo.getFragile() != null ? deliveryInfo.getFragile() : false);
        OrderDto orderDto = mapper.toDto(order);
        BigDecimal deliveryPrice = deliveryApi.deliveryCost(orderDto);
        if (deliveryPrice == null) {
            throw new DeliveryCalculationException("Не удалось рассчитать стоимость доставки для заказа " + orderId);
        }
        order.setDeliveryPrice(deliveryPrice);
        BigDecimal productPrice = order.getProductPrice() != null
                ? order.getProductPrice()
                : BigDecimal.ZERO;
        order.setTotalPrice(productPrice.add(deliveryPrice));
        Order saved = repository.save(order);
        return mapper.toDto(saved);
    }

    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        OrderDto orderDto = mapper.toDto(order);
        BigDecimal productPrice = paymentApi.productCost(orderDto);
        order.setProductPrice(productPrice);
        orderDto.setProductPrice(productPrice);
        BigDecimal totalPrice = paymentApi.getTotalCost(orderDto);
        order.setTotalPrice(totalPrice);
        Order saved = repository.save(order);
        return mapper.toDto(saved);
    }

    private Order getOrderOrThrow(UUID orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(orderId));
    }

    private OrderDto transitionOrderStateAndSave(UUID orderId, OrderState newState, List<OrderState> allowedStates) {
        Order order = getOrderOrThrow(orderId);
        validateStateTransition(order.getState(), newState, allowedStates);
        return applyOrderStateAndSave(order, newState);
    }

    private OrderDto applyOrderStateAndSave(Order order, OrderState newState) {
        order.setState(newState);
        Order saved = repository.save(order);
        return mapper.toDto(saved);
    }

    private void validateCreateOrderRequest(CreateNewOrderRequest request) {
        if (request.getShoppingCart() == null
                || request.getShoppingCart().getProducts() == null
                || request.getShoppingCart().getProducts().isEmpty()) {
            throw new IllegalArgumentException("Корзина не может быть пустой");
        }
    }

    private BigDecimal getProductCostFromPayment(Map<UUID, Long> products) {
        OrderDto orderDto = OrderDto.builder()
                .orderId(UUID.randomUUID())
                .products(products)
                .state(OrderState.NEW)
                .build();
        BigDecimal cost = paymentApi.productCost(orderDto);
        if (cost == null) {
            throw new NoSpecifiedProductInWarehouseException();
        }
        return cost;
    }

    private void planDeliveryForOrder(Order saved, AddressDto toAddress) {

        AddressDto fromAddress = warehouseApi.getWarehouseAddress();
        if (fromAddress == null || toAddress == null) {
            return;
        }
        DeliveryDto deliveryDto = DeliveryDto.builder()
                .orderId(saved.getOrderId())
                .fromAddress(fromAddress)
                .toAddress(toAddress)
                .deliveryState(DeliveryState.CREATED)
                .deliveryVolume(saved.getDeliveryVolume())
                .deliveryWeight(saved.getDeliveryWeight())
                .fragile(saved.getFragile())
                .build();
        DeliveryDto created = deliveryApi.planDelivery(deliveryDto);
        if (created != null && created.getDeliveryId() != null) {
            saved.setDeliveryId(created.getDeliveryId());
            repository.save(saved);
        }
    }

    private void validateReturnedProducts(Map<UUID, Long> orderProducts,
                                          Map<UUID, Long> returnedProducts) {
        for (Map.Entry<UUID, Long> entry : returnedProducts.entrySet()) {
            UUID productId = entry.getKey();
            Long returnQty = entry.getValue();
            Long orderQty = orderProducts.get(productId);
            if (orderQty == null) {
                throw new IllegalArgumentException(
                        "Продукт " + productId + " не содержится в заказе"
                );
            }
            if (returnQty > orderQty) {
                throw new IllegalArgumentException(
                        "Возврат товаров в количестве, превышающем заказанное, невозможен. " + productId
                );
            }
        }
    }

    private void validateStateTransition(OrderState currentState, OrderState newState, List<OrderState> allowedStates) {
        if (!allowedStates.contains(currentState)) {
            throw new InvalidOrderStateException(
                    String.format("Невозможно перейти из состояния %s в %s. Допустимые состояния: %s",
                            currentState, newState, allowedStates)
            );
        }
    }
}