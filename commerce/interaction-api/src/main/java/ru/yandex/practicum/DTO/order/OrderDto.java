package ru.yandex.practicum.DTO.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.enums.order.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    @NotNull
    private UUID orderId;

    private UUID shoppingCartId;

    @NotNull
    private Map<UUID, Long> products;

    private UUID paymentId;

    private UUID deliveryId;

    @NotNull
    private OrderState state;

    private Double deliveryWeight;

    private Double deliveryVolume;

    private Boolean fragile;

    private BigDecimal totalPrice;

    private BigDecimal deliveryPrice;

    private BigDecimal productPrice;
}