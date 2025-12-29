package collector.model.hub.event;

import collector.model.hub.DeviceAction;
import collector.model.hub.ScenarioCondition;
import collector.model.hub.enums.HubEventType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {

    @NotBlank(message = "Name не может быть пустым")
    private String name;

    @NotEmpty(message = "ScenarioCondition не должен быть пустым")
    @Valid
    private List<ScenarioCondition> conditions;

    @NotEmpty(message = "DeviceAction не должен быть пустым")
    @Valid
    private List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
