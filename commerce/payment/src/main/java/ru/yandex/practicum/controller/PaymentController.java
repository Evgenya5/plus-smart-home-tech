package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.payment.PaymentApi;
import ru.yandex.practicum.DTO.order.OrderDto;
import ru.yandex.practicum.DTO.payment.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Validated
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto payment(@Valid @RequestBody OrderDto order) {
        log.info("POST /api/v1/payment");
        PaymentDto dto = paymentService.payment(order);
        return dto;
    }

    @PostMapping("/totalCost")
    public BigDecimal getTotalCost(@Valid @RequestBody OrderDto order) {
        log.info("POST /api/v1/payment/totalCost");
        BigDecimal total = paymentService.getTotalCost(order);
        return total;
    }

    @PostMapping("/productCost")
    public BigDecimal productCost(@Valid @RequestBody OrderDto order) {
        log.info("POST /api/v1/payment/productCost");
        BigDecimal cost = paymentService.productCost(order);
        return cost;
    }

    @PostMapping("/refund")
    public void paymentSuccess(@RequestBody @NotNull UUID paymentId) {
        log.info("POST /api/v1/payment/refund");
        paymentService.paymentSuccess(paymentId);
    }

    @PostMapping("/failed")
    public void paymentFailed(@RequestBody @NotNull UUID paymentId) {
        log.info("POST /api/v1/payment/failed");
        paymentService.paymentFailed(paymentId);
    }
}
