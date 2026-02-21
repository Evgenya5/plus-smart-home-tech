package ru.yandex.practicum.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.DTO.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    ShoppingCart toEntity(ShoppingCartDto dto);

    ShoppingCartDto toDto(ShoppingCart shoppingCart);

}
