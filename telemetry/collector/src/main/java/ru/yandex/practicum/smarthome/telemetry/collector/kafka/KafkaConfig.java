package ru.yandex.practicum.smarthome.telemetry.collector.kafka;

import lombok.Getter;
import lombok.Setter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Properties;

@Getter
@Setter
@Configuration
@ConfigurationProperties("collector.kafka.producer")
public class KafkaConfig {

    // Общие настройки
    private String bootstrapServers;

    // Конфиги
    @Autowired
    private CollectorProducerConfig producerConfig;

    @Bean
    public KafkaClient kafkaClient(
            KafkaProducer<String, SpecificRecordBase> kafkaProducer) {

        return new KafkaClient() {
            @Override
            public Producer<String, SpecificRecordBase> getProducer() {
                return kafkaProducer;
            }

            @Override
            public void close() {
                try {
                    if (kafkaProducer != null) {
                        kafkaProducer.flush();
                        kafkaProducer.close(Duration.ofSeconds(10));
                    }
                } catch (Exception e) {
                }
            }
        };
    }
}

