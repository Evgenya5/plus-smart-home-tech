package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.api.order.OrderApi;
import ru.yandex.practicum.DTO.order.CreateNewOrderRequest;
import ru.yandex.practicum.DTO.order.OrderDto;
import ru.yandex.practicum.DTO.order.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderService orderService;

    @Override
    @GetMapping
    public List<OrderDto> getClientOrders(
            @NotBlank @RequestParam String username) {
        log.info("GET /api/v1/order");
        return orderService.getClientOrders(username);
    }

    @Override
    @PutMapping
    public OrderDto createNewOrder(
            @NotBlank @RequestParam String username,
            @Valid @RequestBody CreateNewOrderRequest request) {
        log.info("PUT /api/v1/order");
        return orderService.createNewOrder(username, request);
    }

    @Override
    @PostMapping("/return")
    public OrderDto productReturn(
            @Valid @RequestBody ProductReturnRequest request) {
        log.info("POST /api/v1/order/return");
        return orderService.productReturn(request);
    }

    @Override
    @PostMapping("/payment")
    public OrderDto payment(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/payment");
        return orderService.payment(orderId);
    }

    @Override
    @PostMapping("/payment/failed")
    public OrderDto paymentFailed(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/payment/failed");
        return orderService.paymentFailed(orderId);
    }

    @Override
    @PostMapping("/delivery")
    public OrderDto delivery(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/delivery");
        return orderService.delivery(orderId);
    }

    @Override
    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailed(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/delivery/failed");
        return orderService.deliveryFailed(orderId);
    }

    @Override
    @PostMapping("/completed")
    public OrderDto complete(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/completed");
        return orderService.complete(orderId);
    }

    @Override
    @PostMapping("/calculate/total")
    public OrderDto calculateTotalCost(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/calculate/total");
        return orderService.calculateTotalCost(orderId);
    }

    @Override
    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryCost(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/calculate/delivery");
        return orderService.calculateDeliveryCost(orderId);
    }

    @Override
    @PostMapping("/assembly")
    public OrderDto assembly(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/assembly");
        return orderService.assembly(orderId);
    }

    @Override
    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailed(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/assembly/failed");
        return orderService.assemblyFailed(orderId);
    }
}