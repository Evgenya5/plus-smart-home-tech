package ru.yandex.practicum.exception.warehouse;

public class NotFoundProductInWarehouseException extends RuntimeException {
    public NotFoundProductInWarehouseException(String message) {
        super(message);
    }
}
