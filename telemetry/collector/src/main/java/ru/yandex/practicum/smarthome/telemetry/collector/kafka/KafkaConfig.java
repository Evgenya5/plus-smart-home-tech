package ru.yandex.practicum.smarthome.telemetry.collector.kafka;

import lombok.Getter;
import lombok.Setter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
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
    private String keySerializer;
    private String valueSerializer;

    /*// Конфиги
    @Autowired
    private CollectorProducerConfig producerConfig;*/

    @Bean
    public KafkaProducer<String, SpecificRecordBase> initProducer(KafkaProperties properties) {
        Properties config = new Properties();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        return new KafkaProducer<>(config);
    }
}

