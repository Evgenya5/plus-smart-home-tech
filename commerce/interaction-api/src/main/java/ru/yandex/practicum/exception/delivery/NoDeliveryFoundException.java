package ru.yandex.practicum.exception.delivery;

import java.util.UUID;

public class NoDeliveryFoundException extends RuntimeException {

    public NoDeliveryFoundException(UUID orderId) {
        super(
                "Не найдена доставка " + orderId
        );
    }
}
