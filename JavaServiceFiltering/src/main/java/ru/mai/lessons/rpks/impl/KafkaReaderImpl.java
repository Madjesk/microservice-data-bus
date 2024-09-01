package ru.mai.lessons.rpks.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import ru.mai.lessons.rpks.KafkaReader;
import ru.mai.lessons.rpks.model.Message;
import ru.mai.lessons.rpks.model.Rule;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

@Setter
@Getter
public class KafkaReaderImpl implements KafkaReader {
    private final KafkaConsumer<String, String> kafkaConsumer;
    private final Config config;
    private Rule[] rules;

    public KafkaReaderImpl(Config config, Rule[] rules) {
        this.config = config;
        this.rules = rules;
        Properties kafkaProps = new Properties();
        Config consumerConfig = config.getConfig("kafka.consumer");
        for (Map.Entry<String, ConfigValue> entry : consumerConfig.entrySet()) {
            kafkaProps.setProperty(entry.getKey(), consumerConfig.getString(entry.getKey()));
        }
        this.kafkaConsumer = new KafkaConsumer<>(kafkaProps, new StringDeserializer(), new StringDeserializer());
        kafkaConsumer.subscribe(Collections.singleton(config.getString("kafka.topic.input")));
    }

    @Override
    public void processing() {
        KafkaWriterImpl kafkaProducer = new KafkaWriterImpl(config);
        RuleProcessorImpl ruleProcessor = new RuleProcessorImpl();

        while (true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(1000));
            for (var consumerRecord : consumerRecords) {
                Message currentMessage = Message.builder().value(consumerRecord.value()).build();
                currentMessage = ruleProcessor.processing(currentMessage, rules);
                if (currentMessage.isFilterState()) {
                    kafkaProducer.processing(currentMessage);
                }
            }
        }
    }
}
