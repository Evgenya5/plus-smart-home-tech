package ru.yandex.practicum.smarthome.telemetry.collector.exception;

public class HubEventPayloadException extends RuntimeException {
    public HubEventPayloadException(String message) {
        super(message);
    }
}
