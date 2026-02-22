package ru.yandex.practicum.DTO.delivery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.DTO.warehouse.AddressDto;
import ru.yandex.practicum.enums.delivery.DeliveryState;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {

    private UUID deliveryId;

    @NotNull
    @Valid
    private AddressDto fromAddress;

    @NotNull
    @Valid
    private AddressDto toAddress;

    @NotNull
    private UUID orderId;

    @NotNull
    private DeliveryState deliveryState;

    private Double deliveryVolume;

    private Double deliveryWeight;

    private Boolean fragile;
}
