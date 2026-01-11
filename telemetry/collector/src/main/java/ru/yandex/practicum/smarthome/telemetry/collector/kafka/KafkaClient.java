package ru.yandex.practicum.smarthome.telemetry.collector.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;

public interface KafkaClient extends AutoCloseable {
    Producer<String, SpecificRecordBase> getProducer();
    void close();
}