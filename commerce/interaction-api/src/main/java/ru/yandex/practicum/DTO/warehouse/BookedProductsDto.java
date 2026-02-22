package ru.yandex.practicum.DTO.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookedProductsDto {

    @NotNull(message = "Вес доставки != null.")
    private Double deliveryWeight;

    @NotNull(message = "Хрупкость != null.")
    private Boolean fragile;

    @NotNull(message = "Объем доставки != null.")
    private Double deliveryVolume;

}
