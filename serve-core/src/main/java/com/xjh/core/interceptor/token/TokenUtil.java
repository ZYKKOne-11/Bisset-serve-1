package com.xjh.core.interceptor.token;

import com.xjh.common.utils.security.DigestUtil;
import com.xjh.common.utils.security.SaltUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

public class TokenUtil {
    public static final int TOKEN2_ENCRYPT_TIMES = 2;
    public static final int SALT_LENGTH = 24;

    public TokenUtil() {
    }

    public static String generateToken(long userId, HttpServletRequest request, HttpServletResponse response) {
        String result1 = DigestUtil.digest(SaltUtil.generateSalt(24) + String.valueOf(userId));
        Cookie token1 = new Cookie("token", Base64.encodeBase64String(result1.getBytes()));
        token1.setPath("/");
        String result2 = DigestUtil.digest(result1, "", 2);
        Cookie token2 = new Cookie("refresh_token", Base64.encodeBase64String(result2.getBytes()));
        token2.setPath("/");
        response.addCookie(token1);
        response.addCookie(token2);
        return result1;
    }

    public static String checkAndReturnToken(HttpServletRequest req) {
        Cookie[] cks = req.getCookies();
        if (cks == null) {
            return null;
        } else {
            String token = null;
            String refreshToken = null;

            for (int i = 0; i < cks.length; ++i) {
                Cookie cookie = cks[i];
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                }

                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }

            if (token != null && refreshToken != null) {
                token = new String(Base64.decodeBase64(token.getBytes()));
                refreshToken = new String(Base64.decodeBase64(refreshToken.getBytes()));
                if (token != null && DigestUtil.digest(token, "", 2).equals(refreshToken)) {
                    return token;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public static String checkToken(String token, String refreshToken) {
        if (token != null && refreshToken != null) {
            token = new String(Base64.decodeBase64(token.getBytes()));
            refreshToken = new String(Base64.decodeBase64(refreshToken.getBytes()));
            return token != null && DigestUtil.digest(token, "", 2).equals(refreshToken) ? token : null;
        } else {
            return null;
        }
    }
}

