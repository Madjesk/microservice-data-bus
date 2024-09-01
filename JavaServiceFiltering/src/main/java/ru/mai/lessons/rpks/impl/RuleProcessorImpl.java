package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.RuleProcessor;
import ru.mai.lessons.rpks.model.Message;
import ru.mai.lessons.rpks.model.Rule;

import java.io.IOException;


@Slf4j
public class RuleProcessorImpl implements RuleProcessor {
    private final ObjectMapper mapper = new ObjectMapper();

    private enum FilterFunctionName {
        EQUALS,
        NOT_EQUALS,
        CONTAINS,
        NOT_CONTAINS
    }

    private boolean checkRule(String fieldValue, Rule rule) {
        FilterFunctionName filterFunctionName = FilterFunctionName.valueOf(rule.getFilterFunctionName().toUpperCase());
        String filterValue = rule.getFilterValue();
        return switch (filterFunctionName) {
            case EQUALS -> fieldValue.equals(filterValue);
            case NOT_EQUALS -> !fieldValue.equals(filterValue);
            case CONTAINS -> fieldValue.contains(filterValue);
            case NOT_CONTAINS -> !fieldValue.contains(filterValue);
        };
    }

    @Override
    public Message processing(Message message, Rule[] rules) {
        if (rules.length == 0) {
            message.setFilterState(false);
            return message;
        }
        try {
            JsonNode jsonNode = mapper.readTree(message.getValue());
            for (Rule rule : rules) {
                JsonNode fieldName = jsonNode.get(rule.getFieldName());
                if (fieldName == null || !fieldName.isValueNode()) {
                    message.setFilterState(false);
                    return message;
                }
                if (!checkRule(fieldName.asText(), rule)) {
                    message.setFilterState(false);
                    return message;
                }
            }
            message.setFilterState(true);
        } catch (IOException e) {
            log.error(e.getMessage());
            message.setFilterState(false);
        }
        return message;
    }
}
