package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.RedisClient;
import ru.mai.lessons.rpks.RuleProcessor;
import ru.mai.lessons.rpks.model.Message;
import ru.mai.lessons.rpks.model.Rule;

@Slf4j
public class RuleProcessorImpl implements RuleProcessor {
    private final ObjectMapper mapper = new ObjectMapper();
    private final RedisClient redisClient;

    public RuleProcessorImpl(Config config) {
        this.redisClient = new RedisClientImpl(config);
    }

    @Override
    public Message processing(Message message, Rule[] rules) {
        message.setDeduplicationState(false);

        if (rules.length == 0) {
            message.setDeduplicationState(true);
            return message;
        }

        if (message.getValue().isEmpty()) {
            return message;
        }
        try {
            long maxTime = 0;
            JsonNode jsonNode = mapper.readTree(message.getValue());
            StringBuilder stringBuilder = new StringBuilder();

            for (Rule rule : rules) {
                if (Boolean.TRUE.equals(rule.getIsActive())) {
                    stringBuilder.append(jsonNode.get(rule.getFieldName()));
                    if (rule.getTimeToLiveSec() > maxTime) {
                        maxTime = rule.getTimeToLiveSec();
                    }
                }
            }

            if (!stringBuilder.toString().isEmpty()) {
                if (redisClient.checkExist(stringBuilder.toString())) {
                    message.setDeduplicationState(false);
                } else {
                    message.setDeduplicationState(true);
                    redisClient.write(stringBuilder.toString(), message, maxTime);
                }
            } else {
                message.setDeduplicationState(true);
            }

            return message;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return message;
    }
}