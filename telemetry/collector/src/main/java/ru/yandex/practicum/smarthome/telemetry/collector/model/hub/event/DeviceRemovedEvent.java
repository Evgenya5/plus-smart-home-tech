package ru.yandex.practicum.smarthome.telemetry.collector.model.hub.event;

import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.enums.HubEventType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemovedEvent extends HubEvent {

    @NotBlank(message = "ID не может быть пустым")
    private String id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}