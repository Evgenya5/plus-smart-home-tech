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
        List<OrderDto> orders = orderService.getClientOrders(username);
        return orders;
    }

    @Override
    @PutMapping
    public OrderDto createNewOrder(
            @NotBlank @RequestParam String username,
            @Valid @RequestBody CreateNewOrderRequest request) {
        log.info("PUT /api/v1/order");
        OrderDto order = orderService.createNewOrder(username, request);
        return order;
    }

    @Override
    @PostMapping("/return")
    public OrderDto productReturn(
            @Valid @RequestBody ProductReturnRequest request) {
        log.info("POST /api/v1/order/return");
        OrderDto order = orderService.productReturn(request);
        return order;
    }

    @Override
    @PostMapping("/payment")
    public OrderDto payment(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/payment");
        OrderDto order = orderService.payment(orderId);
        return order;
    }

    @Override
    @PostMapping("/payment/failed")
    public OrderDto paymentFailed(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/payment/failed");
        OrderDto order = orderService.paymentFailed(orderId);
        return order;
    }

    @Override
    @PostMapping("/delivery")
    public OrderDto delivery(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/delivery");
        OrderDto order = orderService.delivery(orderId);
        return order;
    }

    @Override
    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailed(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/delivery/failed");
        OrderDto order = orderService.deliveryFailed(orderId);
        return order;
    }

    @Override
    @PostMapping("/completed")
    public OrderDto complete(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/completed");
        OrderDto order = orderService.complete(orderId);
        return order;
    }

    @Override
    @PostMapping("/calculate/total")
    public OrderDto calculateTotalCost(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/calculate/total");
        OrderDto order = orderService.calculateTotalCost(orderId);
        return order;
    }

    @Override
    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryCost(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/calculate/delivery");
        OrderDto order = orderService.calculateDeliveryCost(orderId);
        return order;
    }

    @Override
    @PostMapping("/assembly")
    public OrderDto assembly(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/assembly");
        OrderDto order = orderService.assembly(orderId);
        return order;
    }

    @Override
    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailed(
            @RequestBody @NotNull UUID orderId) {
        log.info("POST /api/v1/order/assembly/failed");
        OrderDto order = orderService.assemblyFailed(orderId);
        return order;
    }
}