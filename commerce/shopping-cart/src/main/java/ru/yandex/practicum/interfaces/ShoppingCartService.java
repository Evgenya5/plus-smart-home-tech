package ru.yandex.practicum.interfaces;

import ru.yandex.practicum.DTO.shoppingCart.ChangeProductQuantityRequest;
import ru.yandex.practicum.DTO.shoppingCart.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ShoppingCartService {

    ShoppingCartDto getShoppingCartByUser(String username);

    ShoppingCartDto addProductsAtShoppingCart(String username, Map<UUID, Integer> products);

    void deactivateShoppingCart(String username);

    ShoppingCartDto changeQuantityInTheBasket(String username, ChangeProductQuantityRequest changeQuantity);

    ShoppingCartDto deleteProductsFromShoppingCart(String username, Set<UUID> productIds);

}
