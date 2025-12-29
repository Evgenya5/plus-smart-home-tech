package collector.model.hub;

import collector.model.hub.enums.ConditionType;
import collector.model.hub.enums.OperationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScenarioCondition {

    @NotBlank(message = "SensorID не может быть пустым")
    private String sensorId;

    @NotNull(message = "Type не может быть null")
    private ConditionType type;

    @NotNull(message = "OperationType не может быть null")
    private OperationType operation;

    private Integer value;
}