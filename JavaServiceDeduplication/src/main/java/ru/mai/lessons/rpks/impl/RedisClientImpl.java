package ru.mai.lessons.rpks.impl;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import ru.mai.lessons.rpks.RedisClient;
import ru.mai.lessons.rpks.model.Message;

@Slf4j
public class RedisClientImpl implements RedisClient {
    Jedis jedis;
    JedisPool jedisPool;
    public RedisClientImpl(Config config) {
        String host = config.getString("redis.host");
        int port = config.getInt("redis.port");

        jedisPool = new JedisPool(new JedisPoolConfig(), host, port);
        jedis = jedisPool.getResource();
    }

    @Override
    public boolean checkExist(String value) {
        return jedis.exists(value);
    }

    @Override
    public void write(String key, Message message, long time) {
        jedis.set(key, message.getValue());
        jedis.expire(key, time);
    }
}