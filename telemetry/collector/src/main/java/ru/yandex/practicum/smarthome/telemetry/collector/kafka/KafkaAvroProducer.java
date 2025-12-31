package ru.yandex.practicum.smarthome.telemetry.collector.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.Properties;

@Component
public class KafkaAvroProducer {

    public void sendSensorEvent(SensorEventAvro event) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);

        try (Producer<String, SensorEventAvro> producer = new KafkaProducer<>(config)) {
            ProducerRecord<String, SensorEventAvro> record =
                    new ProducerRecord<>("telemetry.sensors.v1", event);

            producer.send(record);
        }
    }

    public void sendHubEvent(HubEventAvro event) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);

        try (Producer<String, HubEventAvro> producer = new KafkaProducer<>(config)) {
            ProducerRecord<String, HubEventAvro> record =
                    new ProducerRecord<>("telemetry.hubs.v1", event);

            producer.send(record);
        }
    }
}