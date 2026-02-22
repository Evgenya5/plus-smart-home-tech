package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.DeliveryService;
import ru.yandex.practicum.api.delivery.DeliveryApi;
import ru.yandex.practicum.DTO.delivery.DeliveryDto;
import ru.yandex.practicum.DTO.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Validated
public class DeliveryController implements DeliveryApi {

    private final DeliveryService deliveryService;

    @Override
    @PutMapping
    public DeliveryDto planDelivery(@Valid @RequestBody DeliveryDto delivery) {
        log.info("PUT /api/v1/delivery");
        return deliveryService.planDelivery(delivery);
    }

    @Override
    @PostMapping("/cost")
    public BigDecimal deliveryCost(@Valid @RequestBody OrderDto order) {
        log.info("POST /api/v1/delivery/cost");
        double cost = deliveryService.deliveryCost(order);
        return BigDecimal.valueOf(cost);
    }

    @Override
    @PostMapping("/picked")
    public void deliveryPicked(@RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/delivery/picked");
        deliveryService.deliveryPicked(orderId);
    }

    @Override
    @PostMapping("/successful")
    public void deliverySuccessful(@RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/delivery/successful");
        deliveryService.deliverySuccessful(orderId);
    }

    @Override
    @PostMapping("/failed")
    public void deliveryFailed(@RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/delivery/failed");
        deliveryService.deliveryFailed(orderId);
    }
}
