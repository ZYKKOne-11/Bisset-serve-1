package com.xjh.core.service.redis;


import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

public interface RedisService {
    RedisTemplate getRedisTemplate();

    <K> void set(K var1, Object var2, long var3, TimeUnit var5);

    <K> void set(K var1, Object var2, long var3);

    <K, V> void set(K var1, V var2);

    <K, T> T get(K var1, Class<T> var2);

    <K, T> T get(K var1, TypeReference<T> var2);

    <K> String get(K var1);

    <K> void watch(K var1);

    <K> void delete(K var1);

    <K> void expire(K var1, long var2, TimeUnit var4);

    <K> void incrementKeys(List<K> var1);

    <K> void incrementKeys(List<K> var1, long var2);

    <K> Long incrementKey(K var1);

    <K> Long incrementKey(K var1, long var2);

    <K> void deleteMultiKeys(List<K> var1);

    <K> void incrementGroupKey(K var1, K var2);

    <K> void incrementGroupsKey(List<K> var1, K var2);

    <K> void incrementDecrementGroupsKey(List<K> var1, K var2, K var3);

    <K> void incrementGroupsKeys(List<K> var1, List<K> var2);

    <K> void incrementGroupsKeys(List<K> var1, Map<K, Long> var2);

    <K> void incrementGroupKeys(K var1, Map<K, Long> var2);

    <K> Map<Object, Object> getGroup(K var1);

    <K> Object getGroupKeyLongValue(K var1, K var2);

    <K, V> void setGroupKeyValue(K var1, K var2, V var3);

    <K> Boolean isKeyExist(K var1);

    Set<Object> keys(String var1);

    Set<Object> scan(String var1);

    <K, V> void addSet(K var1, V var2);

    <K, V> void removeFromSet(K var1, V var2);
}

