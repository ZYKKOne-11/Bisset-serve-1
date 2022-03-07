package com.xjh.core.service.user.impl;

import com.xjh.common.exception.CommonErrorCode;
import com.xjh.common.exception.CommonException;
import com.xjh.common.po.UserPO;
import com.xjh.common.utils.PropertyLoader;
import com.xjh.common.utils.security.DigestUtil;
import com.xjh.common.vo.UserVO;
import com.xjh.core.config.ApplicationConstant;
import com.xjh.core.interceptor.token.SecurityUtils;
import com.xjh.core.interceptor.token.TokenUtil;
import com.xjh.core.mapper.UserMapper;
import com.xjh.core.service.redis.RedisKeyCenter;
import com.xjh.core.service.redis.RedisService;
import com.xjh.core.service.user.UserService;
import org.apache.catalina.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private static final String DFLAG_USER_LOG = "_self_user_login_debug";
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
    public Boolean register(UserVO userInfo) {
        UserPO userPO = userInfo.getUser();
        String emailRedisKey = getEmailRedisKey(userPO.getEmail());
        String emailCode = redisService.get(emailRedisKey);
        if (emailCode == null || !emailCode.equals(userInfo.getCode())) {
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "邮箱验证码错误或失效，请再试一次");
        }
        userPO.setImg("default");
        userPO.setPassword(Base64.encodeBase64String(userInfo.getUser().getPassword().getBytes()));
        userMapper.insertUser(userPO);
        return true;
    }

    @Override
    public Boolean checkRegisterParam(UserVO userVO) {
        if (userVO.getEmail() != null && !userVO.getEmail().equals("")){
            Integer count = userMapper.selectAccountCountByEmail(userVO.getEmail());
            if (count > 0) {
                logger.debug(DFLAG_USER_LOG+",登录参数校验，邮箱已存在，email: "+userVO.getEmail());
                throw new CommonException(CommonErrorCode.VALIDATE_ERROR, "邮箱已存在,请重新输入");
            }
        }
        if (userVO.getUser().getName() != null && !userVO.getUser().getName().equals("")){
            Integer count = userMapper.selectAccountCountByName(userVO.getUser().getName());
            if (count > 0){
                logger.debug(DFLAG_USER_LOG+",登录参数校验，用户昵称已存在，name: "+userVO.getUser().getName());
                throw new CommonException(CommonErrorCode.VALIDATE_ERROR,"名称已存在，请重新输入");
            }
        }
        return true;
    }

    @Override
    public UserPO login(UserVO userInfo, HttpServletRequest request, HttpServletResponse response) {
        UserPO userPO = null;
        String account = userInfo.getAccount();
        //邮箱登录
        if (isEmail(account)) {
            userPO = userMapper.selectUserByEmail(account);
        } else {
            userPO = userMapper.selectUserByName(account);
        }
        if (userPO == null) {
            throw new CommonException(CommonErrorCode.VALIDATE_ERROR, "该用户名不存在，请重新登录");
        }
        String password = StringUtils.newStringUsAscii(Base64.decodeBase64(userPO.getPassword()));
        if (!Objects.equals(password, userInfo.getUser().getPassword())) {
            throw new CommonException(CommonErrorCode.VALIDATE_ERROR, "用户名或者密码错误,请重新登录");
        }
        String token = TokenUtil.generateToken(userPO.getUserId(), request, response);
        SecurityUtils.setUserInfo(token, userPO, redisService);

        return userPO;
    }

    @Override
    public Boolean changePassword(String oldPassword, String newPassword, HttpServletRequest req) {
        try {
            Long userId = SecurityUtils.getUserId();
            UserPO userInfo = SecurityUtils.getUserInfo();
            String oldPas = userInfo.getPassword();
            String decodePassword = StringUtils.newStringUsAscii(Base64.decodeBase64(oldPas));
            if (!decodePassword.equals(oldPassword)) {
                throw new CommonException(CommonErrorCode.VALIDATE_ERROR, "旧密码不正确，请重新输入");
            }
            newPassword = Base64.encodeBase64String(newPassword.getBytes());
            userInfo.setPassword(newPassword);
            userMapper.updatePasswordById(userId, newPassword);
            String token = TokenUtil.getToken(req);
            String userLoginInfoRedisKey = RedisKeyCenter.getUserLoginInfoRedisKey(token);
            redisService.set(userLoginInfoRedisKey, userInfo, (long) SecurityUtils.TOKEN_TTL_TIME, TimeUnit.SECONDS);
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
        if (!isEmail(emailName)) {
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "邮箱格式非法，请重新输入");
        }
        //设置邮件的正文
        String code = verifyCode(6);
        //发送邮件
        try {
            // 使用JavaMail的MimeMessage，支持更加复杂的邮件格式和内容
            MimeMessage msg = javaMailSender.createMimeMessage();
            // 创建MimeMessageHelper对象，处理MimeMessage的辅助类
            MimeMessageHelper message = new MimeMessageHelper(msg, true);
            //设置邮件主题
            message.setSubject(emailSubject);
            //设置邮件发送者
            message.setFrom(emailFromUserName);
            //设置邮件接收者，可以有多个接收者
            message.setTo(emailName);
            //设置邮件发送日期
            message.setSentDate(new Date());
            message.setText("<head> <base target=\"_blank\" /> <style type=\"text/css\"> ::-webkit-scrollbar { display: none; } </style> <style id=\"cloudAttachStyle\" type=\"text/css\"> #divNeteaseBigAttach, #divNeteaseBigAttach_bak { display: none; } </style> <style id=\"blockquoteStyle\" type=\"text/css\"> blockquote { display: none; } </style> <style type=\"text/css\"> body { font-size: 14px; font-family: arial, verdana, sans-serif; line-height: 1.666; padding: 0; margin: 0; overflow: auto; white-space: normal; word-wrap: break-word; min-height: 100px } td, input, button, select, body { font-family: Helvetica, 'Microsoft Yahei', verdana } pre { white-space: pre-wrap; white-space: -moz-pre-wrap; white-space: -pre-wrap; white-space: -o-pre-wrap; word-wrap: break-word; width: 95% } th, td { font-family: arial, verdana, sans-serif; line-height: 1.666 } img { border: 0 } header, footer, section, aside, article, nav, hgroup, figure, figcaption { display: block } blockquote { margin-right: 0px } </style> </head> <body tabindex=\"0\" role=\"listitem\"> <table width=\"700\" border=\"0\" align=\"center\" cellspacing=\"0\" style=\"width:700px;\"> <tbody> <tr> <td> <h1 style=\"color: #002752;font-size: 48px;font-weight: bolder;font-family: sans-serif;\">NiceCoder</h1> <div style=\"width:700px;margin:0 auto;border-bottom:1px solid #ccc;margin-bottom:30px;\"> <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"700\" height=\"39\" style=\"font:12px Tahoma, Arial, 宋体;\"> <tbody> <tr> <td width=\"210\"></td> </tr> </tbody> </table> </div> <div style=\"width:680px;padding:0 10px;margin:0 auto;\"> <div style=\"line-height:1.5;font-size:14px;margin-bottom:25px;color:#4d4d4d;\"> <strong style=\"display:block;margin-bottom:15px;\">尊敬的用户：<span style=\"color:#f60;font-size: 16px;\"></span>您好！</strong> <strong style=\"display:block;margin-bottom:15px;\"> 您正在进行<span style=\"color: #002752;font-size: 24px\"> 邮箱验证 </span>操作，请在验证码输入框中输入：<span style=\"color:#0076f4;font-size: 24px\">" + code + "</span>，以完成操作。 </strong> </div> <div style=\"margin-bottom:30px;\"> <small style=\"display:block;margin-bottom:20px;font-size:12px;\"> <p style=\"color:#747474;\"> 注意：此操作可能会修改您的密码、登录邮箱或绑定登录邮箱。如非本人操作，请及时登录并修改密码以保证帐户安全 <br>（工作人员不会向你索取此验证码，请勿泄漏！) </p> </small> </div> </div> <div style=\"width:700px;margin:0 auto;\"> <div style=\"padding:10px 10px 0;border-top:1px solid #ccc;color:#747474;margin-bottom:20px;line-height:1.3em;font-size:12px;\"> <p>此为系统邮件，请勿回复<br> 请保管好您的邮箱，避免账号被他人盗用 </p> <p>NiceCoder</p> </div> </div> </td> </tr> </tbody> </table> </body>", true); // 内容！
            javaMailSender.send(msg);
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

    @Override
    public UserPO query() {
        try {
            UserPO userInfo = SecurityUtils.getUserInfo();
            return userInfo;
        } catch (Exception e) {
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "用户缓存信息失效，请重新登录");
        }
    }

    @Override
    public List<UserPO> selectUserList(List<Long> ids) {
        try{
            List<UserPO> res = userMapper.selectUserList(ids);
            return res;
        }catch (Exception e){
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR,"查询User_List异常，请稍后再试");
        }
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

    /**
     * @return 如果是符合邮箱格式的字符串, 返回true, 否则为false
     */
    public static boolean isEmail(String content) {
        String pattern = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";
        boolean isMatch = Pattern.matches(pattern, content);
        return isMatch;
    }
}
