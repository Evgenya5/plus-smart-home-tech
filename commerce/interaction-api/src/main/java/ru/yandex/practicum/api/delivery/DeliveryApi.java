package ru.yandex.practicum.api.delivery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.DTO.delivery.DeliveryDto;
import ru.yandex.practicum.DTO.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "delivery")
public interface DeliveryApi {

    @PutMapping("/api/v1/delivery")
    DeliveryDto planDelivery(@Valid @RequestBody DeliveryDto delivery);

    @PostMapping("/api/v1/delivery/cost")
    BigDecimal deliveryCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/api/v1/delivery/picked")
    void deliveryPicked(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/delivery/successful")
    void deliverySuccessful(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/delivery/failed")
    void deliveryFailed(@RequestBody @NotNull UUID orderId);
}
