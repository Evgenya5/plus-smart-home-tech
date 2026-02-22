package ru.yandex.practicum.DTO.shoppingCart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeProductQuantityRequest {

    @NotNull
    private UUID productId;

    @NotNull
    @Min(value = 1, message = "Количество товаров не может быть меньше 1.")
    private Integer newQuantity;
}
