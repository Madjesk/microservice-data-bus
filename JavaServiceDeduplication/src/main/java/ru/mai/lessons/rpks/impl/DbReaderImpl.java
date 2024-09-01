package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.DbReader;

import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import ru.mai.lessons.rpks.model.Rule;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class DbReaderImpl implements DbReader {
    private final HikariDataSource hikariDataSource;

    DbReaderImpl(Config config) {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getString("db.jdbcUrl"));
        hikariConfig.setDriverClassName(config.getString("db.driver"));
        hikariConfig.setUsername(config.getString("db.user"));
        hikariConfig.setPassword(config.getString("db.password"));
        hikariDataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public Rule[] readRulesFromDB() {
        try (Connection connection = hikariDataSource.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.POSTGRES);
            var rules = dslContext.select().from("deduplication_rules").fetch();
            Rule[] result = new Rule[rules.size()];
            int index = 0;

            for (var ruleDB : rules) {
                Rule rule = new Rule();
                rule.setRuleId((Long) ruleDB.getValue("rule_id"));
                rule.setFieldName((String) ruleDB.getValue("field_name"));
                rule.setDeduplicationId((Long) ruleDB.getValue("deduplication_id"));
                rule.setIsActive((Boolean) ruleDB.getValue("is_active"));
                rule.setTimeToLiveSec((Long) ruleDB.getValue("time_to_live_sec"));
                result[index++] = rule;
            }
            return result;
        } catch (SQLException e) {
            log.error("Something wrong with connection", e);
        }
        return new Rule[0];
    }
}
