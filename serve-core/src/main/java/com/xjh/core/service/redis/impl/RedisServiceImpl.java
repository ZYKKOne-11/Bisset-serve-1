package com.xjh.core.service.redis.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.xjh.common.utils.json.JsonUtils;
import com.xjh.core.service.redis.ObjectRedisTemplate;
import com.xjh.core.service.redis.RedisService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.ScanOptions.ScanOptionsBuilder;
import org.springframework.lang.NonNull;

public class RedisServiceImpl implements RedisService {
    private ObjectRedisTemplate objectRedisTemplate;

    public RedisServiceImpl(ObjectRedisTemplate objectRedisTemplate) {
        this.objectRedisTemplate = objectRedisTemplate;
    }

    public RedisTemplate getRedisTemplate() {
        return this.objectRedisTemplate;
    }

    public <K> void set(K key, Object value, long timeout, TimeUnit timeUnit) {
        this.objectRedisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public <K> void set(K key, Object value, long timeOutSeconds) {
        this.objectRedisTemplate.opsForValue().set(key, value, timeOutSeconds, TimeUnit.SECONDS);
    }

    public <K, T> void set(K key, T value) {
        Long expire = this.objectRedisTemplate.getExpire(key);
        this.objectRedisTemplate.opsForValue().set(key, value);
        if (expire != null && expire > 0L) {
            this.objectRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }

    }

    public <K, T> T get(K key, Class<T> elementType) {
        String value = (String) this.objectRedisTemplate.opsForValue().get(key);
        return value == null ? null : JsonUtils.string2Object(value, elementType);
    }

    public <K, T> T get(K key, TypeReference<T> type) {
        String value = (String) this.objectRedisTemplate.opsForValue().get(key);
        return value == null ? null : JsonUtils.string2Object(value, type);
    }

    public <K> String get(K key) {
        return (String) this.objectRedisTemplate.opsForValue().get(key);
    }

    public <K> void watch(K key) {
        this.objectRedisTemplate.watch(key);
    }

    public <K> void delete(K key) {
        this.objectRedisTemplate.delete(key);
    }

    public <K> void expire(K key, long timeout, TimeUnit unit) {
        this.objectRedisTemplate.expire(key, timeout, unit);
    }

    public <K> void incrementKeys(List<K> keyList) {
        this.incrementKeys(keyList, 1L);
    }

    public <K> void incrementKeys(final List<K> keyList, final long delta) {
        this.objectRedisTemplate.execute(new SessionCallback<Object>() {
            public <K, V> Object execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                ObjectRedisTemplate redisTemplate = (ObjectRedisTemplate) operations;
                operations.multi();
                Iterator var3 = keyList.iterator();

                while (var3.hasNext()) {
                    Object key = var3.next();
                    redisTemplate.opsForValue().increment(key, delta);
                    redisTemplate.expire(key, 45L, TimeUnit.DAYS);
                }

                operations.exec();
                return null;
            }
        });
    }

    public <K> Long incrementKey(K key) {
        return this.incrementKey(key, 1L);
    }

    public <K> Long incrementKey(K key, long delta) {
        return this.objectRedisTemplate.opsForValue().increment(key, delta);
    }

    public <K> void deleteMultiKeys(final List<K> keyList) {
        this.objectRedisTemplate.execute(new SessionCallback<String>() {
            public <K, V> String execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                ObjectRedisTemplate redisTemplate = (ObjectRedisTemplate) operations;
                operations.multi();
                Iterator var3 = keyList.iterator();

                while (var3.hasNext()) {
                    Object key = var3.next();
                    redisTemplate.delete(key);
                }

                operations.exec();
                return null;
            }
        });
    }

    public <K> void incrementGroupKey(K groupName, K keyName) {
        this.objectRedisTemplate.opsForHash().increment(groupName, keyName, 1L);
    }

    public <K> void incrementGroupsKey(final List<K> groupsName, final K keyName) {
        this.objectRedisTemplate.execute(new SessionCallback<String>() {
            public <K, V> String execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                ObjectRedisTemplate redisTemplate = (ObjectRedisTemplate) operations;
                operations.multi();
                Iterator var3 = groupsName.iterator();

                while (var3.hasNext()) {
                    Object groupName = var3.next();
                    redisTemplate.opsForHash().increment(groupName, keyName, 1L);
                }

                operations.exec();
                return null;
            }
        });
    }

    public <K> void incrementDecrementGroupsKey(final List<K> groupsName, final K decrementKeyName, final K incrementKeyName) {
        this.objectRedisTemplate.execute(new SessionCallback<String>() {
            public <K, V> String execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                ObjectRedisTemplate redisTemplate = (ObjectRedisTemplate) operations;
                operations.multi();
                Iterator var3 = groupsName.iterator();

                Object groupName;
                while (var3.hasNext()) {
                    groupName = var3.next();
                    redisTemplate.opsForHash().increment(groupName, decrementKeyName, -1L);
                }

                var3 = groupsName.iterator();

                while (var3.hasNext()) {
                    groupName = var3.next();
                    redisTemplate.opsForHash().increment(groupName, incrementKeyName, 1L);
                }

                operations.exec();
                return null;
            }
        });
    }

    public <K> void incrementGroupsKeys(final List<K> groupsName, final List<K> keysName) {
        this.objectRedisTemplate.execute(new SessionCallback<String>() {
            public <K, V> String execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                ObjectRedisTemplate redisTemplate = (ObjectRedisTemplate) operations;
                operations.multi();
                Iterator var3 = groupsName.iterator();

                while (var3.hasNext()) {
                    Object groupName = var3.next();
                    keysName.forEach((keyName) -> {
                        redisTemplate.opsForHash().increment(groupName, keyName, 1L);
                    });
                }

                operations.exec();
                return null;
            }
        });
    }

    public <K> void incrementGroupsKeys(final List<K> groupsName, final Map<K, Long> map) {
        this.objectRedisTemplate.execute(new SessionCallback<String>() {
            public <K, V> String execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                ObjectRedisTemplate redisTemplate = (ObjectRedisTemplate) operations;
                operations.multi();
                Iterator var3 = groupsName.iterator();

                while (var3.hasNext()) {
                    Object groupName = var3.next();
                    map.forEach((key, val) -> {
                        redisTemplate.opsForHash().increment(groupName, key, val);
                    });
                }

                operations.exec();
                return null;
            }
        });
    }

    public <K> void incrementGroupKeys(final K groupName, final Map<K, Long> map) {
        this.objectRedisTemplate.execute(new SessionCallback<String>() {
            public <K, V> String execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                ObjectRedisTemplate redisTemplate = (ObjectRedisTemplate) operations;
                operations.multi();
                map.forEach((key, val) -> {
                    redisTemplate.opsForHash().increment(groupName, key, val);
                });
                operations.exec();
                return null;
            }
        });
    }

    public <K> Map<Object, Object> getGroup(K groupName) {
        return this.objectRedisTemplate.opsForHash().entries(groupName);
    }

    public <K> Object getGroupKeyLongValue(K groupName, K keyName) {
        return this.objectRedisTemplate.opsForHash().get(groupName, keyName);
    }

    public <K, V> void setGroupKeyValue(K groupName, K keyName, V value) {
        this.objectRedisTemplate.opsForHash().put(groupName, keyName, value);
    }

    public <K> Boolean isKeyExist(K key) {
        return this.objectRedisTemplate.hasKey(key);
    }

    public Set<Object> keys(String keyPattern) {
        return (Set) (StringUtils.isBlank(keyPattern) ? new HashSet(0) : this.objectRedisTemplate.keys(keyPattern));
    }

    public Set<Object> scan(String keyPattern) {
        return (Set) this.objectRedisTemplate.execute((RedisCallback<Set<Object>>) (connection) -> {
            Set<Object> binaryKeys = new HashSet();
            Cursor cursor = connection.scan((new ScanOptionsBuilder()).match(keyPattern).count(10000L).build());
            while (cursor.hasNext()) {
                binaryKeys.add(new String((byte[]) cursor.next()));
            }

            return binaryKeys;
        });
    }

    public <K, V> void addSet(K key, V value) {
        this.objectRedisTemplate.opsForSet().add(key, new Object[]{value});
    }

    public <K, V> void removeFromSet(K key, V value) {
        this.objectRedisTemplate.opsForSet().remove(key, new Object[]{value});
    }
}

