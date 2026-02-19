package ru.yandex.practicum.DTO.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddProductToWarehouseRequest {
    @NotNull(message = "ID продукта != null.")
    private UUID productId;

    @NotNull(message = "Количество != null")
    @Positive(message = "Количество доожно быть положительным числом.")
    private Long quantity;
}
