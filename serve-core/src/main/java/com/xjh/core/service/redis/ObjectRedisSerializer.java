package com.xjh.core.service.redis;


import com.xjh.common.utils.json.JsonUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

public class ObjectRedisSerializer implements RedisSerializer<Object> {
    private final Charset charset;

    public ObjectRedisSerializer() {
        this(StandardCharsets.UTF_8);
    }

    public ObjectRedisSerializer(Charset charset) {
        Assert.notNull(charset, "Charset must not be null!");
        this.charset = charset;
    }

    public byte[] serialize(Object obj) throws SerializationException {
        String string;
        if (obj instanceof String) {
            string = (String) obj;
        } else {
            string = JsonUtils.object2String(obj);
        }

        return string == null ? null : string.getBytes(this.charset);
    }

    public Object deserialize(byte[] bytes) throws SerializationException {
        return bytes == null ? null : new String(bytes, this.charset);
    }
}

