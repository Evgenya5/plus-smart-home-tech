package ru.yandex.practicum.api.shoppingCart;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.DTO.shoppingCart.ChangeProductQuantityRequest;
import ru.yandex.practicum.DTO.shoppingCart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartApi {

    @PutMapping
    ShoppingCartDto addProductsAtShoppingCart(@RequestParam String username,
                                                              @RequestBody Map<UUID, Long> productQuantities);

    @GetMapping
    ShoppingCartDto getShoppingCartByUser(@RequestParam String username);

    @PostMapping("change-quantity")
    ShoppingCartDto changeQuantityInTheBasket(
            @RequestParam
            String username,
            @Valid @RequestBody ChangeProductQuantityRequest changeQuantity);

    @DeleteMapping
    void deactivateShoppingCart(@RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto deleteProductsFromShoppingCart(@RequestParam String username,
                                                                   @RequestParam List<UUID> productIds);
}
