package ru.yandex.practicum.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface KafkaClient extends AutoCloseable {
    Producer<String, SpecificRecordBase> getProducer();
    Consumer<String, SensorEventAvro> getConsumer();
    void close();
}
