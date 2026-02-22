package ru.yandex.practicum.exception.order;

public class NoSpecifiedProductInWarehouseException extends RuntimeException {

    public NoSpecifiedProductInWarehouseException() {
        super(
                "Нет заказываемого товара на складе"
        );
    }
}