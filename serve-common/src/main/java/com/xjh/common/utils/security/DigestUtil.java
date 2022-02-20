package com.xjh.common.utils.security;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class DigestUtil {
    public static final Integer OPEN_API_DIGEST_TIMES = 1;

    public DigestUtil() {
    }

    public static String digest(String password) {
        return digest(password, SaltUtil.getEncryptTimes());
    }

    public static String digest(String password, int encryptTimes) {
        String salt = SaltUtil.generateSalt();
        return digest(password, salt, encryptTimes);
    }

    public static String digest(String password, String salt, int encryptTimes) {
        try {
            MessageDigest digest = DigestUtils.getSha256Digest();
            if (salt != null) {
                digest.update(salt.getBytes("UTF-8"));
            }

            byte[] result = digest.digest(password.getBytes("UTF-8"));

            for (int i = 1; i < encryptTimes; ++i) {
                digest.reset();
                result = digest.digest(result);
            }

            return Hex.encodeHexString(result);
        } catch (Exception var6) {
            var6.printStackTrace();
            return null;
        }
    }

    public static String sha256(String originString) {
        return Objects.isNull(originString) ? null : Hashing.sha256().hashString(originString, StandardCharsets.UTF_8).toString();
    }
}

