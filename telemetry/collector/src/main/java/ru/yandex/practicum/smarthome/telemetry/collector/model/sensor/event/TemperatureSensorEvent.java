package ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.event;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.enums.SensorEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class TemperatureSensorEvent extends SensorEvent {

    @NotNull(message = "temperatureC не может быть null")
    private Integer temperatureC;

    @NotNull(message = "temperatureF не может быть null")
    private Integer temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
