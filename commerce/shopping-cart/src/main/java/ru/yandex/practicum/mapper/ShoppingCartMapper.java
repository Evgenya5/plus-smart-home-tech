package ru.yandex.practicum.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.DTO.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    ShoppingCart toEntity(ShoppingCartDto dto);

    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "shoppingCartId", ignore = true)
    default void addOnlyNewProducts(ShoppingCartDto dto, @MappingTarget ShoppingCart cart) {

        dto.getProducts().forEach((productId, quantity) ->
                cart.getProducts().putIfAbsent(productId, quantity)
        );
    }
}
