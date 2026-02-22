package ru.yandex.practicum.exception.warehouse;

public class ProductAlreadyExistInWarehouseException extends RuntimeException {
    public ProductAlreadyExistInWarehouseException(String message) {
        super(message);
    }
}
