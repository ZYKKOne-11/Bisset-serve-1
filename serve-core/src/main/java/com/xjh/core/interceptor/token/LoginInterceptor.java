package com.xjh.core.interceptor.token;

import com.xjh.core.interceptor.BaseLoginInterceptor;
import com.xjh.core.service.redis.RedisService;
import com.xjh.core.interceptor.token.SecurityUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoginInterceptor extends BaseLoginInterceptor implements HandlerInterceptor {
    @Resource
    private RedisService redisService;

    public LoginInterceptor() {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return this.doPreHandle(request, response, handler, this.redisService, "");
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) throws Exception {
        SecurityUtils.remove();
    }
}

