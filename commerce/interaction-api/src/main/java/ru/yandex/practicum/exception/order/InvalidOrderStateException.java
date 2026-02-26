package ru.yandex.practicum.exception.order;

public class InvalidOrderStateException extends RuntimeException {

    public InvalidOrderStateException(String message) {
        super(
                message
        );
    }
}
