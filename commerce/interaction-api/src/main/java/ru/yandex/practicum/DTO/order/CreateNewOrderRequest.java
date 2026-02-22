package ru.yandex.practicum.DTO.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.DTO.warehouse.AddressDto;
import ru.yandex.practicum.DTO.warehouse.ShoppingCartDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNewOrderRequest {

    @NotNull
    private ShoppingCartDto shoppingCart;

    @NotNull
    private AddressDto deliveryAddress;
}