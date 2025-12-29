package ru.yandex.practicum.smarthome.telemetry.collector.model.hub;

import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.enums.ActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceAction {

    @NotBlank(message = "SensorID не может быть пустым")
    private String sensorId;

    @NotNull(message = "ActionType не может быть null")
    private ActionType type;

    private Integer value;
}
