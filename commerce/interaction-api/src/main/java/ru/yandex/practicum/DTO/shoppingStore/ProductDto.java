package ru.yandex.practicum.DTO.shoppingStore;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.enums.shoppingStore.ProductCategory;
import ru.yandex.practicum.enums.shoppingStore.ProductState;
import ru.yandex.practicum.enums.shoppingStore.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private UUID productId;

    @NotBlank(message = "Название товара не может быть пустой строкой.")
    private String productName;

    @NotBlank(message = "Описание товара != null.")
    private String description;

    private String imageSrc;

    @NotNull(message = "Значение остатка != null.")
    private QuantityState quantityState;

    @NotNull(message = "Значение статуса корзины != null.")
    private ProductState productState;

    private ProductCategory productCategory;

    @Min(value = 1, message = "Мин. цена 1.")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;
}
