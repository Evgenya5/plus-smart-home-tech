package ru.yandex.practicum.exception.warehouse;

public class EmptyShoppingCart extends RuntimeException {
    public EmptyShoppingCart(String message) {
        super(message);
    }
}
