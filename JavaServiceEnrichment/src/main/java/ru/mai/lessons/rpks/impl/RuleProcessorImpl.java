package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import ru.mai.lessons.rpks.MongoDBClientEnricher;
import ru.mai.lessons.rpks.RuleProcessor;
import ru.mai.lessons.rpks.model.Message;
import ru.mai.lessons.rpks.model.Rule;

@Slf4j
public class RuleProcessorImpl implements RuleProcessor {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MongoDBClientEnricher mongoDBClientEnricher;

    public RuleProcessorImpl(Config config) {
        this.mongoDBClientEnricher = new MongoDBClientEnricherImpl(config);
    }

    @Override
    public Message processing(Message message, Rule[] rules) {
        if (rules.length == 0) return message;
        try {
            JsonNode jsonMessage = objectMapper.readTree(message.getValue());
            for (Rule rule : rules) {
                Document doc = new Document();
                String fieldNameEnrichment = rule.getFieldNameEnrichment();
                String fieldName = rule.getFieldName();
                String fieldValue = rule.getFieldValue();

                doc.put(fieldNameEnrichment, fieldValue);
                Document enrichDoc = mongoDBClientEnricher.getEnrichRules(doc);
                if (enrichDoc != null) {
                    ((ObjectNode) jsonMessage).set(fieldName, objectMapper.readTree(enrichDoc.toJson()));
                } else {
                    ((ObjectNode) jsonMessage).put(fieldName, rule.getFieldValueDefault());
                }
            }
            message.setValue(jsonMessage.toString());

            return message;
        } catch (Exception ex) {
            mongoDBClientEnricher.closeClient();
            log.error(ex.getMessage());
        }
        return message;
    }
}
