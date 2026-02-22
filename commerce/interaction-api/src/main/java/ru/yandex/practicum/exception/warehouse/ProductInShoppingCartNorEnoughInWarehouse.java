package ru.yandex.practicum.exception.warehouse;

public class ProductInShoppingCartNorEnoughInWarehouse extends RuntimeException {
    public ProductInShoppingCartNorEnoughInWarehouse(String message) {
        super(message);
    }
}
