package com.xjh.core.interceptor;


import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.xjh.common.exception.CommonErrorCode;
import com.xjh.common.model.ResultModel;
import com.xjh.common.po.UserPO;
import com.xjh.core.interceptor.token.SecurityUtils;
import com.xjh.core.interceptor.token.TokenUtil;
import com.xjh.core.service.redis.RedisKeyCenter;
import com.xjh.core.service.redis.RedisService;

public class BaseLoginInterceptor {

    public BaseLoginInterceptor() {
    }

    public boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler, RedisService redisService, String stuff) throws Exception {
        UserPO user = null;
        String userToken = TokenUtil.checkAndReturnToken(request);
        String ip;
        if (userToken != null) {
            ip = RedisKeyCenter.getUserLoginInfoRedisKey(userToken + stuff);
            user = (UserPO) redisService.get(ip, UserPO.class);
            if (null == user) {
                writeErrorMsg(response, ResultModel.fail(CommonErrorCode.USER_NOT_LOGIN, "用户未登录，请重新登录"));
                return false;
            }
            SecurityUtils.flushUserInfo(userToken + stuff, redisService);
            ip = getRemoteIpAddress(request);
            SecurityUtils.flushRemoteIpAddress(ip);
            SecurityUtils.setUserInfo(userToken + stuff, user, redisService);
            return true;
        } else {
            writeErrorMsg(response, ResultModel.fail(CommonErrorCode.USER_NOT_LOGIN, "用户未登录，请重新登录"));
            return false;
        }
    }

    static void writeErrorMsg(HttpServletResponse response, ResultModel resultModel) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("apiSuccess", "false");
        PrintWriter writer = response.getWriter();
        Throwable var3 = null;

        try {
            writer.print(JSON.toJSONString(resultModel));
        } catch (Throwable var12) {
            var3 = var12;
            throw var12;
        } finally {
            if (writer != null) {
                if (var3 != null) {
                    try {
                        writer.close();
                    } catch (Throwable var11) {
                        var3.addSuppressed(var11);
                    }
                } else {
                    writer.close();
                }
            }

        }

    }


    private String getRemoteIpAddress(HttpServletRequest request) {
        String ip = null;
        String ipAddresses = request.getHeader("X-Forwarded-For");
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("X-Real-IP");
        }

        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }

        return !ip.equals("127.0.0.1") && !ip.endsWith("0:0:0:0:0:0:1") ? ip : "127.0.0.1";
    }

}
