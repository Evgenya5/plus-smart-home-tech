package ru.yandex.practicum.exception.order;

public class DeliveryCalculationException extends RuntimeException  {

    public DeliveryCalculationException(String message) {
        super(
                message
        );
    }
}
