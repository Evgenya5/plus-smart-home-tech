package ru.yandex.practicum.exception.order;

import java.util.UUID;

public class NoOrderFoundException extends RuntimeException {

    public NoOrderFoundException(UUID orderId) {
        super(
                "Не найден заказ" + orderId
        );
    }
}