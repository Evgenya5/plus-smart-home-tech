package ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.event;

import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.enums.SensorEventType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class SwitchSensorEvent extends SensorEvent {
    @NotNull(message = "State не может быть null")
    private Boolean state;

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}
