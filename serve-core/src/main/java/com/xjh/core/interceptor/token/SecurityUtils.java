package com.xjh.core.interceptor.token;


import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.xjh.common.po.UserPO;
import com.xjh.core.service.redis.RedisKeyCenter;
import com.xjh.core.service.redis.RedisService;
import org.apache.commons.lang.StringUtils;

public class SecurityUtils {
    public static final Integer TOKEN_TTL_TIME = 604800;
    private static ThreadLocal<UserPO> threadLocal = new ThreadLocal();
    private static ThreadLocal<Long> doorForOpeUserIdThreadLocal = new ThreadLocal();
    private static ThreadLocal<String> remoteIpAddress = new ThreadLocal();

    public SecurityUtils() {
    }

    public static UserPO getUserInfo() {
        return (UserPO) threadLocal.get();
    }

    public static final Long getUserId() {
        return ((UserPO) threadLocal.get()).getUserId();
    }

    public static void setUserInfo(String token, UserPO user, RedisService redisService) {
        String userLoginInfoRedisKey = RedisKeyCenter.getUserLoginInfoRedisKey(token);
        redisService.set(userLoginInfoRedisKey, user, (long) TOKEN_TTL_TIME, TimeUnit.SECONDS);
        threadLocal.set(user);
    }


    public static Long getDoorOpeUserId() {
        Long doorOpeUserId = (Long) doorForOpeUserIdThreadLocal.get();
        return Objects.isNull(doorOpeUserId) ? 0L : doorOpeUserId;
    }

    public static void flushRemoteIpAddress(String ipAddress) {
        if (StringUtils.isNotBlank(ipAddress)) {
            remoteIpAddress.set(ipAddress);
        }

    }

    public static String getRemoteIpAddress() {
        return (String) remoteIpAddress.get();
    }

    public static void flushUserInfo(String token, RedisService redisService) {
        String userLoginInfoRedisKey = RedisKeyCenter.getUserLoginInfoRedisKey(token);
        threadLocal.set(redisService.get(userLoginInfoRedisKey, UserPO.class));
    }

    public static void remove() {
        threadLocal.remove();
    }

    public static void setUserInfoForDoor(String token, UserPO opeUser, RedisService redisService) {
        redisService.set(RedisKeyCenter.getDoorOpeUserIdRedisKey(token), String.valueOf(opeUser.getUserId()), (long) TOKEN_TTL_TIME, TimeUnit.SECONDS);
    }
}
