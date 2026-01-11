package ru.yandex.practicum.smarthome.telemetry.collector.model.hub.event;

import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.enums.DeviceType;
import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.enums.HubEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {

    @NotBlank(message = "ID не может быть пустым")
    private String id;

    @NotNull(message = "DeviceType не может быть null")
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}