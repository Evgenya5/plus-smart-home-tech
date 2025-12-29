package collector.model.sensor.event;

import collector.model.sensor.enums.SensorEventType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class MotionSensorEvent extends SensorEvent {

    @NotNull(message = "LinkQuality связи не может быть null")
    private Integer linkQuality;

    @NotNull(message = "Motion не может быть null")
    private Boolean motion;

    @NotNull(message = "Voltage не может быть null")
    private Integer voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
