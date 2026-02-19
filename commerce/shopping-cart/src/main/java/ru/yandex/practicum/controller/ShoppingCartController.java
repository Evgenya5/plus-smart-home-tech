package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.DTO.shoppingCart.ChangeProductQuantityRequest;
import ru.yandex.practicum.DTO.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.interfaces.ShoppingCartService;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController {
    private final ShoppingCartService service;

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto addProductsAtShoppingCart(@RequestParam
                                                     @NotBlank(message = "Имя пользователя != null.") String username,
                                                     @RequestBody(required = false) Map<UUID, Integer> products) {
        log.info("PUT. addProductsAtShoppingCart: {}", username);
        return service.addProductsAtShoppingCart(username, products);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto getShoppingCartByUser(@RequestParam
                                                 @NotBlank(message = "Имя пользователя != null.") String username) {
        log.info("GET. getShoppingCartByUser: {}", username);
        return service.getShoppingCartByUser(username);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deactivateShoppingCart(@RequestParam
                                            @NotBlank(message = "Имя пользователя != null.") String username) {
        log.info("DELETE. deactivateShoppingCart: {}", username);
        service.deactivateShoppingCart(username);
    }


    @PostMapping("change-quantity")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto changeQuantityInTheBasket(
            @RequestParam
            @NotBlank(message = "Имя пользователя != null.") String username,
            @Valid @RequestBody ChangeProductQuantityRequest changeQuantity) {
        log.info("POST. changeQuantityInTheBasket: {}", username);
        return service.changeQuantityInTheBasket(username, changeQuantity);
    }

    @PostMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto deleteProductsFromShoppingCart(@RequestParam
                                                          @NotBlank(message = "Имя пользователя != null.") String username,
                                                          @RequestBody
                                                          @NotNull Set<UUID> productIds) {
        log.info("POST. deleteProductsFromShoppingCart: {}", username);
        return service.deleteProductsFromShoppingCart(username, productIds);
    }
}
