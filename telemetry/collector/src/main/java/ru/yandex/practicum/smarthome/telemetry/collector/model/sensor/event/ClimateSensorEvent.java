package ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.event;

import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.enums.SensorEventType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class ClimateSensorEvent extends SensorEvent {

    @NotNull(message = "temperatureC не может быть пустым")
    private Integer temperatureC;

    @NotNull(message = "humidity не может быть пустым")
    private Integer humidity;

    @NotNull(message = "co2Level не может быть пустым")
    private Integer co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
