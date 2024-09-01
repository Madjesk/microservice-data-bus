package ru.mai.lessons.rpks.impl;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.DbReader;
import ru.mai.lessons.rpks.Service;
import ru.mai.lessons.rpks.model.Rule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServiceFiltering implements Service {
    private Rule[] rules;
    private DbReader dbReader;
    private KafkaReaderImpl kafkaConsumer;

    @Override
    public void start(Config config) {
        dbReader = new DbReaderImpl(config);
        rules = dbReader.readRulesFromDB();
        kafkaConsumer = new KafkaReaderImpl(config, rules);

        ScheduledExecutorService executorService = null;
        try {
            executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleAtFixedRate(this::updateRules, 0,
                    config.getInt("application.updateIntervalSec"),
                    TimeUnit.SECONDS);

            kafkaConsumer.processing();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            if (executorService != null) {
                executorService.shutdown();
            }
        }
    }

    private void updateRules() {
        rules = dbReader.readRulesFromDB();
        kafkaConsumer.setRules(rules);
    }

}
