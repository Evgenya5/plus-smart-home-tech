package ru.yandex.practicum.api.payment;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.DTO.order.OrderDto;
import ru.yandex.practicum.DTO.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "payment")
public interface PaymentApi {

    @PostMapping("/api/v1/payment")
    PaymentDto payment(@Valid @RequestBody OrderDto order);

    @PostMapping("/api/v1/payment/totalCost")
    BigDecimal getTotalCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/api/v1/payment/productCost")
    BigDecimal productCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/api/v1/payment/refund")
    void paymentSuccess(@RequestBody UUID paymentId);

    @PostMapping("/api/v1/payment/failed")
    void paymentFailed(@RequestBody UUID paymentId);
}
