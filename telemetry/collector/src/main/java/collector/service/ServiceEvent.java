package collector.service;

import collector.model.hub.DeviceAction;
import collector.model.hub.ScenarioCondition;
import collector.model.hub.event.*;
import collector.model.sensor.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import collector.kafka.KafkaAvroProducer;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceEvent {
    private final KafkaAvroProducer kafkaProducer;

    public void processSensorEvent(SensorEvent event) {
        SensorEventAvro avroEvent = mapToAvro(event);
        kafkaProducer.sendSensorEvent(avroEvent);
    }

    public void processHubEvent(HubEvent event) {
        HubEventAvro avroEvent = mapToAvro(event);
        kafkaProducer.sendHubEvent(avroEvent);
    }

    private SensorEventAvro mapToAvro(SensorEvent event) {

        Object sensorPayload = switch (event.getType()) {
            case TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorEvent tempEvent = (TemperatureSensorEvent) event;
                yield TemperatureSensorAvro.newBuilder()
                        .setTemperatureF(tempEvent.getTemperatureF())
                        .setTemperatureC(tempEvent.getTemperatureC())
                        .build();
            }
            case LIGHT_SENSOR_EVENT -> {
                LightSensorEvent light = (LightSensorEvent) event;
                yield LightSensorAvro.newBuilder()
                        .setLinkQuality(light.getLinkQuality())
                        .setLuminosity(light.getLuminosity())
                        .build();
            }
            case CLIMATE_SENSOR_EVENT -> {
                ClimateSensorEvent climate = (ClimateSensorEvent) event;
                yield ClimateSensorAvro.newBuilder()
                        .setHumidity(climate.getHumidity())
                        .setCo2Level(climate.getCo2Level())
                        .setTemperatureC(climate.getTemperatureC())
                        .build();
            }
            case MOTION_SENSOR_EVENT -> {
                MotionSensorEvent motion = (MotionSensorEvent) event;
                yield MotionSensorAvro.newBuilder()
                        .setLinkQuality(motion.getLinkQuality())
                        .setMotion(motion.getMotion())
                        .setVoltage(motion.getVoltage())
                        .build();
            }
            case SWITCH_SENSOR_EVENT -> {
                SwitchSensorEvent switchSensor = (SwitchSensorEvent) event;
                yield SwitchSensorAvro.newBuilder()
                        .setState(switchSensor.getState())
                        .build();
            }
        };

        SensorEventPayload eventPayload = SensorEventPayload.newBuilder()
                .setPayload(sensorPayload)
                .build();

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(eventPayload)
                .build();
    }

    private HubEventAvro mapToAvro(HubEvent event) {

        Object hubPayload = switch (event.getType()) {
            case DEVICE_ADDED -> {
                DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) event;
                yield DeviceAddedEventAvro.newBuilder()
                        .setId(deviceAddedEvent.getId())
                        .setType(DeviceTypeAvro.valueOf(deviceAddedEvent.getDeviceType().name()))
                        .build();
            }
            case DEVICE_REMOVED -> {
                DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) event;
                yield DeviceRemovedEventAvro.newBuilder().setId(deviceRemovedEvent.getId()).build();
            }
            case SCENARIO_ADDED -> {
                ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) event;

                List<DeviceActionAvro> deviceActionAvros = new ArrayList<>();
                List<ScenarioConditionAvro> scenarioConditionAvros = new ArrayList<>();

                for (DeviceAction deviceAction : scenarioAddedEvent.getActions()) {
                    DeviceActionAvro deviceActionAvro = DeviceActionAvro.newBuilder()
                            .setType(ActionTypeAvro.valueOf(deviceAction.getType().name()))
                            .setSensorId(deviceAction.getSensorId())
                            .setValue(deviceAction.getValue())
                            .build();
                    deviceActionAvros.add(deviceActionAvro);
                }

                for (ScenarioCondition scenarioCondition : scenarioAddedEvent.getConditions()) {
                    ScenarioConditionAvro scenarioConditionAvro = ScenarioConditionAvro.newBuilder()
                            .setType(ConditionTypeAvro.valueOf(scenarioCondition.getType().name()))
                            .setOperation(ConditionOperationAvro.valueOf(scenarioCondition.getOperation().name()))
                            .setSensorId(scenarioCondition.getSensorId())
                            .setValue(scenarioCondition.getValue())
                            .build();
                    scenarioConditionAvros.add(scenarioConditionAvro);
                }

                yield ScenarioAddedEventAvro.newBuilder()
                        .setActions(deviceActionAvros)
                        .setConditions(scenarioConditionAvros)
                        .setName(scenarioAddedEvent.getName())
                        .build();
            }
            case SCENARIO_REMOVED -> {
                ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) event;
                yield ScenarioRemovedEventAvro.newBuilder().setName(scenarioRemovedEvent.getName()).build();
            }
        };

        HubEventPayload hubEventPayload = HubEventPayload.newBuilder()
                .setPayload(hubPayload)
                .build();

        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(hubEventPayload)
                .build();
    }
}
