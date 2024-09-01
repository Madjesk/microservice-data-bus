package ru.mai.lessons.rpks.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import ru.mai.lessons.rpks.KafkaWriter;
import ru.mai.lessons.rpks.model.Message;

import java.util.Map;
import java.util.Properties;

public class KafkaWriterImpl implements KafkaWriter {
    private final KafkaProducer<String, String> kafkaProducer;
    private final Config config;

    public KafkaWriterImpl(Config config) {
        this.config = config;
        Properties kafkaProperties = new Properties();
        Config consumerConfig = config.getConfig("kafka.producer");
        for (Map.Entry<String, ConfigValue> entry : consumerConfig.entrySet()) {
            kafkaProperties.setProperty(entry.getKey(), consumerConfig.getString(entry.getKey()));
        }
        kafkaProducer = new KafkaProducer<>(kafkaProperties, new StringSerializer(), new StringSerializer());
    }

    // отправляет сообщения с filterState = true в выходной топик. Конфигурация берется из файла *.conf
    @Override
    public void processing(Message message) {
        kafkaProducer.send(new ProducerRecord<>(config.getString("kafka.topic.output"), message.getValue()));
    }
}
