package ru.yandex.practicum.smarthome.telemetry.collector.kafka;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class HubEventAvroSerializer implements Serializer<HubEventAvro> {
    private final EncoderFactory encoderFactory = EncoderFactory.get();

    @Override
    public byte[] serialize(String s, HubEventAvro event) {
        if (event == null) {
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = encoderFactory.binaryEncoder(outputStream, null);
            SpecificDatumWriter<HubEventAvro> datumWriter = new SpecificDatumWriter<>(HubEventAvro.class);

            datumWriter.write(event, encoder);
            encoder.flush();

            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new SerializationException("Ошибка сериализации экземпляра HubEventAvro", e);
        }
    }
}