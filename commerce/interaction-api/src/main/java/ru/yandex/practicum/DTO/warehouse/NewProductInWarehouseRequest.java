package ru.yandex.practicum.DTO.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewProductInWarehouseRequest {

    @NotNull
    private UUID productId;

    private Boolean fragile;

    @NotNull
    private DimensionDto dimension;

    @NotNull
    @Min(value = 1)
    private Double weight;

    @Min(value = 0, message = "Количество товара не может быть отрицательным.")
    @Builder.Default
    private Long quantity = 0L;
}
