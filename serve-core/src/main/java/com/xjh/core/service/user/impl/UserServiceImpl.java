package com.xjh.core.service.user.impl;

import com.xjh.common.exception.CommonErrorCode;
import com.xjh.common.exception.CommonException;
import com.xjh.common.po.UserPO;
import com.xjh.common.utils.PropertyLoader;
import com.xjh.common.utils.security.DigestUtil;
import com.xjh.core.config.ApplicationConstant;
import com.xjh.core.interceptor.token.SecurityUtils;
import com.xjh.core.interceptor.token.TokenUtil;
import com.xjh.core.mapper.UserMapper;
import com.xjh.core.service.redis.RedisKeyCenter;
import com.xjh.core.service.redis.RedisService;
import com.xjh.core.service.user.UserService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private static final Integer TOKEN_EMAIL_TIME = 300;
    private static final Long TOKEN_EMAIL_SEND_TIME_MILLIS = 60000L;
    private String emailSubject = PropertyLoader.getProperty("mail.subject");
    private String emailFromUserName = PropertyLoader.getProperty("mail.from.username");

    @Resource
    UserMapper userMapper;

    @Resource
    RedisService redisService;

    @Resource
    JavaMailSender javaMailSender;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Boolean register(UserPO userInfo, String code) {
        Integer count = userMapper.selectAccountCount(userInfo);
        if (count > 0) {
            throw new CommonException(CommonErrorCode.VALIDATE_ERROR, "该用户名已存在");
        }
        String emailRedisKey = getEmailRedisKey(userInfo.getEmail());
        String emailCode = redisService.get(emailRedisKey);
        if (!emailCode.equals(code)) {
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "邮箱验证码错误，请重新输入");
        }
        userInfo.setPassword(DigestUtil.digest(userInfo.getPassword(), ApplicationConstant.firstEncryptSalt, ApplicationConstant.firstDigestTimes));
        userMapper.insertUser(userInfo);
        return true;
    }

    @Override
    public UserPO login(UserPO userInfo, HttpServletRequest request, HttpServletResponse response) {
        UserPO userPO = userMapper.selectUserByAccount(userInfo);
        if (userPO == null) {
            throw new CommonException(CommonErrorCode.VALIDATE_ERROR, "该用户名不存在，请重新登录");
        }
        String password = DigestUtil.digest(userInfo.getPassword(), ApplicationConstant.firstEncryptSalt, ApplicationConstant.firstDigestTimes);
        if (!Objects.equals(password, userPO.getPassword())) {
            throw new CommonException(CommonErrorCode.VALIDATE_ERROR, "用户名或者密码错误,请重新登录");
        }
        String token = TokenUtil.generateToken(userPO.getUserId(), request, response);
        SecurityUtils.setUserInfo(token, userPO, redisService);
        return userPO;
    }

    @Override
    public Boolean changePassword(String password) {
        try {
            Long userId = SecurityUtils.getUserId();
            String digestPassword = DigestUtil.digest(password, ApplicationConstant.firstEncryptSalt, ApplicationConstant.firstDigestTimes);
            userMapper.insertPasswordById(userId, digestPassword);
            return true;
        } catch (Exception e) {
            logger.error("修改密码异常，err: " + e.getMessage());
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "修改密码失败，请重试");
        }
    }

    @Override
    public Boolean changeUserEmail(UserPO userInfo) {
        return null;
    }

    @Override
    public Boolean sendEmail(String emailName) {
        if (!checkEmailParam(emailName)) {
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "一份钟内请不要重复邮箱验证");
        }
        //构建一个邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        //设置邮件主题
        message.setSubject(emailSubject);
        //设置邮件发送者
        message.setFrom(emailFromUserName);
        //设置邮件接收者，可以有多个接收者
        message.setTo(emailName);
        //设置邮件发送日期
        message.setSentDate(new Date());
        //设置邮件的正文
        String code = verifyCode(6);
        message.setText("【Nice Coder】您好，您的邮箱验证码为" + code + "，有效期为三分钟。如非您本人操作，请忽视本条消息，谢谢。");
        //发送邮件
        try {
            javaMailSender.send(message);
            String token = Base64.encodeBase64String(emailName.getBytes());
            String emailRedisKey = RedisKeyCenter.getUserEmailInfoRedisKey(token);
            String emailSendTimeRedisKey = RedisKeyCenter.getUserEmailTimeInfoRediskey(token);
            logger.info("邮箱：" + emailName + "的验证信息发送成功,验证码：" + code + " redis-token: " + emailRedisKey);
            redisService.set(emailRedisKey, code, (long) TOKEN_EMAIL_TIME, TimeUnit.SECONDS);
            redisService.set(emailSendTimeRedisKey, System.currentTimeMillis(), (long) TOKEN_EMAIL_TIME, TimeUnit.SECONDS);
        } catch (MailSendException e) {
            logger.error("用户邮箱：" + emailName + " 不存在，发送验证消息失败");
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "邮箱校验失败，请重新输入邮箱账号");
        } catch (Exception e) {
            logger.error("邮件信息发送异常");
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "邮件发送失败，请重试");
        }
        return true;
    }

    private String verifyCode(int n) {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; i++) {
            int ran1 = r.nextInt(10);
            sb.append(String.valueOf(ran1));
        }
        return sb.toString();
    }

    private String getEmailRedisKey(String emailName) {
        String token = Base64.encodeBase64String(emailName.getBytes());
        String userEmailInfoRedisKey = RedisKeyCenter.getUserEmailInfoRedisKey(token);
        return userEmailInfoRedisKey;
    }

    private Boolean checkEmailParam(String emailName) {
        String token = Base64.encodeBase64String(emailName.getBytes());
        String userEmailInfoRedisKey = RedisKeyCenter.getUserEmailTimeInfoRediskey(token);
        Long emailSendTime = redisService.get(userEmailInfoRedisKey, Long.class);
        Long timeMillis = System.currentTimeMillis();
        if (emailSendTime == null || (timeMillis - emailSendTime) > TOKEN_EMAIL_SEND_TIME_MILLIS) {
            return true;
        }
        return false;
    }
}
