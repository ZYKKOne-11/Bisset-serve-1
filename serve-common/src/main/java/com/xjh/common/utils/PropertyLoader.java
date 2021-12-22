package com.xjh.common.utils;

import javaslang.control.Try;
import org.apache.commons.lang3.StringUtils;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class PropertyLoader {
    private static final Logger logger = LoggerFactory.getLogger(PropertyLoader.class);
    private static Properties RESOURCE_BUNDLE;

    public PropertyLoader() {
    }

    public static String getProperty(String key) {
        String property;
        try {
            property = RESOURCE_BUNDLE.getProperty(key);
            logger.debug("load property, key={}, val={}.", key, property);
        } catch (MissingResourceException var3) {
            throw new RuntimeException("can't load context file,key:" + key, var3);
        }

        Assert.assertNotNull(property, "key=" + key + ", 值为空，请检查配置文件。");
        return property;
    }

    public static int getIntProperty(String key) {
        return Integer.valueOf(getProperty(key));
    }

    public static Long getLongProperty(String key) {
        return Long.valueOf(getProperty(key));
    }

    public static Set<Long> getSetLongProperty(String key) {
        String str = getProperty(key);
        String[] strList = StringUtils.split(str, ",");
        Set<Long> set = new HashSet();
        String[] var4 = strList;
        int var5 = strList.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            String istr = var4[var6];
            Long id = 0L;

            try {
                id = Long.valueOf(istr);
            } catch (Exception var10) {
                continue;
            }

            set.add(id);
        }

        return set;
    }

    public static Set<String> getSetStringProperty(String key) {
        String str = getProperty(key);
        String[] strList = StringUtils.split(str, ",");
        return new HashSet(Arrays.asList(strList));
    }

    public static boolean getBooleanProperty(String key) {
        return Boolean.valueOf(getProperty(key));
    }

    public static Properties loadProperties(String path) {
        Properties properties = new Properties();

        try {
            InputStreamReader isr = new InputStreamReader(loadAsInputStream(path), "UTF-8");
            BufferedReader bf = new BufferedReader(isr);
            properties.load(bf);
            return properties;
        } catch (IOException var4) {
            throw new RuntimeException("配置文件解析失败。", var4);
        }
    }

    private static InputStream loadAsInputStream(String path) {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (stream == null) {
            throw new RuntimeException("配置文件加载失败或文件不存在:" + path);
        } else {
            logger.debug("加载配置文件, path={}.", path);
            return stream;
        }
    }

//    static {
//        Properties properties = new Properties();
//        properties.putAll(loadProperties("context.properties"));
//        Try.run(() -> {
//            properties.putAll(loadProperties("application.properties"));
//        }).onFailure((e) -> {
//            logger.warn("spring.profiles.active", e.getMessage());
//        });
//        Try.run(() -> {
//            properties.putAll(loadProperties("healthcheck.properties"));
//        }).onFailure((e) -> {
//            logger.warn("healthcheck.properties", e.getMessage());
//        });
//        RESOURCE_BUNDLE = properties;
//    }
}
