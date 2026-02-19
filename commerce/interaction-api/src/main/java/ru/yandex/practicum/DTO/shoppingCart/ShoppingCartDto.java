package ru.yandex.practicum.DTO.shoppingCart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartDto {
    private UUID shoppingCartId;

    private Map<
            @NotNull(message = "ID != null.")
                    UUID,
            @Min(value = 1, message = "Количество товаров не может быть меньше 1.")
                    Integer>
            products;
}
