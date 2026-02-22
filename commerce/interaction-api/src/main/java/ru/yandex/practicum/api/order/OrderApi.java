package ru.yandex.practicum.api.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.DTO.order.CreateNewOrderRequest;
import ru.yandex.practicum.DTO.order.OrderDto;
import ru.yandex.practicum.DTO.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order")
public interface OrderApi {

    @GetMapping("/api/v1/order")
    List<OrderDto> getClientOrders(@NotBlank @RequestParam String username);

    @PutMapping("/api/v1/order")
    OrderDto createNewOrder(@NotBlank @RequestParam String username,
                                            @Valid @RequestBody CreateNewOrderRequest request);

    @PostMapping("/api/v1/order/return")
    OrderDto productReturn(@Valid @RequestBody ProductReturnRequest request);

    @PostMapping("/api/v1/order/payment")
    OrderDto payment(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/payment/failed")
    OrderDto paymentFailed(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/delivery")
    OrderDto delivery(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/delivery/failed")
    OrderDto deliveryFailed(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/completed")
    OrderDto complete(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/calculate/total")
    OrderDto calculateTotalCost(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/calculate/delivery")
    OrderDto calculateDeliveryCost(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/assembly")
    OrderDto assembly(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/assembly/failed")
    OrderDto assemblyFailed(@RequestBody @NotNull UUID orderId);
}