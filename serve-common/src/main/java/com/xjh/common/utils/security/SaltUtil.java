package com.xjh.common.utils.security;


import java.util.Random;

public class SaltUtil {
    private static final Integer SALT_SIZE = 8;

    public SaltUtil() {
    }

    public static int getEncryptTimes() {
        int encryptTimes = (int) (1.0D + Math.random() * 4.0D);
        return encryptTimes;
    }

    public static String generateSalt() {
        String s = "";
        Random r = new Random();

        for (int i = 0; i < SALT_SIZE; ++i) {
            s = s + (char) (48 + r.nextInt(43));
        }

        return s.toLowerCase();
    }

    public static String generateSalt(int length) {
        String s = "";
        Random r = new Random();

        for (int i = 0; i < length; ++i) {
            s = s + (char) (48 + r.nextInt(43));
        }

        return s.toLowerCase();
    }

    public static void main(String[] args) {
        System.out.println(generateSalt());
    }
}

