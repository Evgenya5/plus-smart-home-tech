package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

@Slf4j
@Component
public class AggregationStarter {

    @Value("${aggregator.kafka.topics.sensors-events}")
    private String sensorsEventsTopic;

    @Value("${aggregator.kafka.topics.snapshots-events}")
    private String snapshotsEventsTopic;

    @Value("${aggregator.kafka.listener.poll-timeout}")
    private long pollMillis;

    private final Producer<String, SpecificRecordBase> producer;
    private final Consumer<String, SpecificRecordBase> consumer;
    private final SnapshotAggregator snapshotAggregator;

    public AggregationStarter(KafkaClient kafkaClient, SnapshotAggregator snapshotAggregator) {
        this.producer = kafkaClient.getProducer();
        this.consumer = kafkaClient.getConsumer();
        this.snapshotAggregator = snapshotAggregator;
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(sensorsEventsTopic));
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(pollMillis));
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorEventAvro event = (SensorEventAvro) record.value();

                    Optional<SensorsSnapshotAvro> updatedSnapshot = snapshotAggregator.updateState(event);

                    updatedSnapshot.ifPresent(snapshot -> {
                        SensorsSnapshotAvro snap = updatedSnapshot.get();
                        Future<RecordMetadata> futureResult = producer.send(new ProducerRecord<>(
                                snapshotsEventsTopic,
                                null,
                                System.currentTimeMillis(),
                                snapshot.getHubId(),
                                snap
                        ));
                        try {
                            RecordMetadata metadata = futureResult.get();
                            // логирование успеха
                            consumer.commitSync();
                            log.debug("Данные успешно отправлены");
                        } catch (Exception e) {
                            log.warn("Не удалось записать снапшот ", e);
                        }

                    });
                }
                //consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
            // Игнорируем при выключении
        } catch (Exception e) {
            // Игнорируем ошибки
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                consumer.close();
                producer.close();
            }
        }
    }
}