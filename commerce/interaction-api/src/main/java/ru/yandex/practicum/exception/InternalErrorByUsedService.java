package ru.yandex.practicum.exception;

public class InternalErrorByUsedService extends RuntimeException {
    public InternalErrorByUsedService(String serviceName) {
        super("Ошибка при использовании внутреннего сервиса " + serviceName + ", обратитесь к сопровождению сервиса");
    }
}
