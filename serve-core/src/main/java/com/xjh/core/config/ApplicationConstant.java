package com.xjh.core.config;

import com.xjh.common.utils.PropertyLoader;

public class ApplicationConstant {
    public static final String firstEncryptSalt = PropertyLoader.getProperty("first.encrypt.salt");
    public static final Integer firstDigestTimes = Integer.valueOf(PropertyLoader.getProperty("first.digest.times"));
}
