package ru.yandex.practicum.smarthome.telemetry.collector.controller;

import ru.yandex.practicum.smarthome.telemetry.collector.model.hub.event.HubEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.model.sensor.event.SensorEvent;
import ru.yandex.practicum.smarthome.telemetry.collector.service.ServiceEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final ServiceEvent serviceEvent;

    @PostMapping("/sensors")
    public ResponseEntity<Void> collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        serviceEvent.processSensorEvent(event);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hubs")
    public ResponseEntity<Void> collectHubEvent(@Valid @RequestBody HubEvent event) {
        serviceEvent.processHubEvent(event);
        return ResponseEntity.ok().build();
    }
}
