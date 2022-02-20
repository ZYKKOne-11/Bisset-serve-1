package com.xjh.core.service.redis;


import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

public class ObjectRedisTemplate extends RedisTemplate<Object, Object> {
    public ObjectRedisTemplate() {
        RedisSerializer<Object> objectRedisSerializer = new ObjectRedisSerializer();
        this.setKeySerializer(objectRedisSerializer);
        this.setValueSerializer(objectRedisSerializer);
        this.setHashKeySerializer(objectRedisSerializer);
        this.setHashValueSerializer(objectRedisSerializer);
    }

    public ObjectRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        this.setConnectionFactory(connectionFactory);
    }
}

