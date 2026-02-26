package ru.yandex.practicum.exception.order;

public class NotAuthorizedUserException extends RuntimeException {

    public NotAuthorizedUserException() {
        super(
                "Имя пользователя не должно быть пустым"
        );
    }
}