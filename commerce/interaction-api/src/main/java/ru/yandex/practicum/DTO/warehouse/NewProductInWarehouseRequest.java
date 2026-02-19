package ru.yandex.practicum.DTO.warehouse;

import jakarta.validation.Valid;
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

    @NotNull(message = "ID продукта != null.")
    private UUID productId;

    @NotNull(message = "Описание товара != null.")
    @Valid
    private DimensionDto dimension;

    @Min(value = 0, message = "Количество товара не может быть отрицательным.")
    @Builder.Default
    private Long quantity = 0L;

    @Min(value = 1, message = "Вес товара не может быть меньше 1.")
    @Builder.Default
    private Double weight = 1.0;

    @Builder.Default
    private Boolean fragile = true;
}
