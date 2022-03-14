package com.xjh.core.config;

import com.xjh.core.interceptor.token.LoginInterceptor;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootConfiguration
public class WebMVCConfig implements WebMvcConfigurer {

    @Resource
    LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 白名单
        List<String> excludePath = new ArrayList<>();
        excludePath.add("/user/login");
        excludePath.add("/user/register");
        excludePath.add("/user/send-email");
        excludePath.add("/user/check-param");
        excludePath.add("/web/#/login");
        excludePath.add("/hotspot/query");
        excludePath.add("/hostel/rank/query");

        excludePath.add("/hostel/share/query");
        excludePath.add("/hostel/discuss/query");

        excludePath.add("/**/*.gif");
        excludePath.add("/**/*.jpg");
        excludePath.add("/**/*.jpeg");
        excludePath.add("/**/*.png");
        excludePath.add("/**/*.bmp");
        excludePath.add("/**/*.swf");
        excludePath.add("/**/*.ico");
        excludePath.add("/**/*.css");
        excludePath.add("/**/*.js");
        excludePath.add("/**/error");
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(excludePath);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

    }
}
