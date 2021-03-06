package com.xjh.core.service.user.impl;

import com.xjh.common.consts.HostelConstant;
import com.xjh.common.exception.CommonErrorCode;
import com.xjh.common.exception.CommonException;
import com.xjh.common.po.UserPO;
import com.xjh.common.utils.HostelRankingUtils;
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
import org.springframework.data.redis.core.ZSetOperations;
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
import java.util.*;
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
        try {
            UserPO userPO = userInfo.getUser();
            String emailRedisKey = getEmailRedisKey(userPO.getEmail());
            String emailCode = redisService.get(emailRedisKey);
            if (emailCode == null || !emailCode.equals(userInfo.getCode())) {
                throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "????????????????????????????????????????????????");
            }
            userPO.setImg("default");
            userPO.setPassword(Base64.encodeBase64String(userInfo.getUser().getPassword().getBytes()));
            userMapper.insertUser(userPO);
            userExecAddScore(String.valueOf(userPO.getUserId()), HostelConstant.USER_REGISTER_SCORE);
            return true;
        }catch (Exception e){
            logger.error("?????????????????????User???"+userInfo.toString());
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR,e.getMessage());
        }
    }

    @Override
    public Boolean checkRegisterParam(UserVO userVO) {
        try{
            if (userVO.getUser().getEmail() != null && !userVO.getUser().getEmail().equals("")){
                Integer count = userMapper.selectAccountCountByEmail(userVO.getUser().getEmail());
                if (count > 0) {
                    logger.debug(DFLAG_USER_LOG+",???????????????????????????????????????email: "+userVO.getEmail());
                    return false;
                }
            }
            if (userVO.getUser().getName() != null && !userVO.getUser().getName().equals("")){
                Integer count = userMapper.selectAccountCountByName(userVO.getUser().getName());
                if (count > 0){
                    logger.debug(DFLAG_USER_LOG+",?????????????????????????????????????????????name: "+userVO.getUser().getName());
                    return false;
                }
            }
            return true;
        }catch(Exception e){
            //????????????
            logger.error("?????????????????????????????????");
            return false;
        }
    }

    @Override
    public UserPO login(UserVO userInfo, HttpServletRequest request, HttpServletResponse response) {
        UserPO userPO = null;
        String account = userInfo.getAccount();
        //????????????
        if (isEmail(account)) {
            userPO = userMapper.selectUserByEmail(account);
        } else {
            userPO = userMapper.selectUserByName(account);
        }
        if (userPO == null) {
            throw new CommonException(CommonErrorCode.VALIDATE_ERROR, "???????????????????????????????????????");
        }
        String password = StringUtils.newStringUsAscii(Base64.decodeBase64(userPO.getPassword()));
        if (!Objects.equals(password, userInfo.getUser().getPassword())) {
            throw new CommonException(CommonErrorCode.VALIDATE_ERROR, "???????????????????????????,???????????????");
        }
        String token = TokenUtil.generateToken(userPO.getUserId(), request, response);
        SecurityUtils.setUserInfo(token, userPO, redisService);
        userExecAddScore(String.valueOf(userPO.getUserId()), HostelConstant.USER_REGISTER_SCORE);
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
                throw new CommonException(CommonErrorCode.VALIDATE_ERROR, "????????????????????????????????????");
            }
            newPassword = Base64.encodeBase64String(newPassword.getBytes());
            userInfo.setPassword(newPassword);
            userMapper.updatePasswordById(userId, newPassword);
            String token = TokenUtil.getToken(req);
            String userLoginInfoRedisKey = RedisKeyCenter.getUserLoginInfoRedisKey(token);
            redisService.set(userLoginInfoRedisKey, userInfo, (long) SecurityUtils.TOKEN_TTL_TIME, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            logger.error("?????????????????????err: " + e.getMessage());
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "??????????????????????????????");
        }
    }

    @Override
    public Boolean changeUserEmail(UserPO userInfo) {
        return null;
    }

    @Override
    public Boolean sendEmail(String emailName) {
        if (!checkEmailParam(emailName)) {
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "???????????????????????????????????????");
        }
        if (!isEmail(emailName)) {
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "????????????????????????????????????");
        }
        //?????????????????????
        String code = verifyCode(6);
        //????????????
        try {
            // ??????JavaMail???MimeMessage?????????????????????????????????????????????
            MimeMessage msg = javaMailSender.createMimeMessage();
            // ??????MimeMessageHelper???????????????MimeMessage????????????
            MimeMessageHelper message = new MimeMessageHelper(msg, true);
            //??????????????????
            message.setSubject(emailSubject);
            //?????????????????????
            message.setFrom(emailFromUserName);
            //????????????????????????????????????????????????
            message.setTo(emailName);
            //????????????????????????
            message.setSentDate(new Date());
            message.setText("<head> <base target=\"_blank\" /> <style type=\"text/css\"> ::-webkit-scrollbar { display: none; } </style> <style id=\"cloudAttachStyle\" type=\"text/css\"> #divNeteaseBigAttach, #divNeteaseBigAttach_bak { display: none; } </style> <style id=\"blockquoteStyle\" type=\"text/css\"> blockquote { display: none; } </style> <style type=\"text/css\"> body { font-size: 14px; font-family: arial, verdana, sans-serif; line-height: 1.666; padding: 0; margin: 0; overflow: auto; white-space: normal; word-wrap: break-word; min-height: 100px } td, input, button, select, body { font-family: Helvetica, 'Microsoft Yahei', verdana } pre { white-space: pre-wrap; white-space: -moz-pre-wrap; white-space: -pre-wrap; white-space: -o-pre-wrap; word-wrap: break-word; width: 95% } th, td { font-family: arial, verdana, sans-serif; line-height: 1.666 } img { border: 0 } header, footer, section, aside, article, nav, hgroup, figure, figcaption { display: block } blockquote { margin-right: 0px } </style> </head> <body tabindex=\"0\" role=\"listitem\"> <table width=\"700\" border=\"0\" align=\"center\" cellspacing=\"0\" style=\"width:700px;\"> <tbody> <tr> <td> <h1 style=\"color: #002752;font-size: 48px;font-weight: bolder;font-family: sans-serif;\">NiceCoder</h1> <div style=\"width:700px;margin:0 auto;border-bottom:1px solid #ccc;margin-bottom:30px;\"> <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"700\" height=\"39\" style=\"font:12px Tahoma, Arial, ??????;\"> <tbody> <tr> <td width=\"210\"></td> </tr> </tbody> </table> </div> <div style=\"width:680px;padding:0 10px;margin:0 auto;\"> <div style=\"line-height:1.5;font-size:14px;margin-bottom:25px;color:#4d4d4d;\"> <strong style=\"display:block;margin-bottom:15px;\">??????????????????<span style=\"color:#f60;font-size: 16px;\"></span>?????????</strong> <strong style=\"display:block;margin-bottom:15px;\"> ???????????????<span style=\"color: #002752;font-size: 24px\"> ???????????? </span>?????????????????????????????????????????????<span style=\"color:#0076f4;font-size: 24px\">" + code + "</span>????????????????????? </strong> </div> <div style=\"margin-bottom:30px;\"> <small style=\"display:block;margin-bottom:20px;font-size:12px;\"> <p style=\"color:#747474;\"> ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? <br>???????????????????????????????????????????????????????????????) </p> </small> </div> </div> <div style=\"width:700px;margin:0 auto;\"> <div style=\"padding:10px 10px 0;border-top:1px solid #ccc;color:#747474;margin-bottom:20px;line-height:1.3em;font-size:12px;\"> <p>?????????????????????????????????<br> ?????????????????????????????????????????????????????? </p> <p>NiceCoder</p> </div> </div> </td> </tr> </tbody> </table> </body>", true); // ?????????
            javaMailSender.send(msg);
            String token = Base64.encodeBase64String(emailName.getBytes());
            String emailRedisKey = RedisKeyCenter.getUserEmailInfoRedisKey(token);
            String emailSendTimeRedisKey = RedisKeyCenter.getUserEmailTimeInfoRediskey(token);
            logger.info("?????????" + emailName + "???????????????????????????,????????????" + code + " redis-token: " + emailRedisKey);
            redisService.set(emailRedisKey, code, (long) TOKEN_EMAIL_TIME, TimeUnit.SECONDS);
            redisService.set(emailSendTimeRedisKey, System.currentTimeMillis(), (long) TOKEN_EMAIL_TIME, TimeUnit.SECONDS);
        } catch (MailSendException e) {
            logger.error("???????????????" + emailName + " ????????????????????????????????????");
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "????????????????????????????????????????????????");
        } catch (Exception e) {
            logger.error("????????????????????????");
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "??????????????????????????????");
        }
        return true;
    }

    @Override
    public UserPO query() {
        try {
            UserPO userInfo = SecurityUtils.getUserInfo();
            return userInfo;
        } catch (Exception e) {
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "??????????????????????????????????????????");
        }
    }

    @Override
    public List<UserPO> selectUserList(List<Long> ids) {
        try{
            List<UserPO> res = userMapper.selectUserList(ids);
            return res;
        }catch (Exception e){
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR,"??????User_List????????????????????????");
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
     * @return ???????????????????????????????????????, ??????true, ?????????false
     */
    public static boolean isEmail(String content) {
        String pattern = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";
        boolean isMatch = Pattern.matches(pattern, content);
        return isMatch;
    }

    private Boolean userExecAddScore(String userId,Double score){
        try{
            String overAllKey = RedisKeyCenter.getUserOverAllRankingRedisKey();
            String monthKey = RedisKeyCenter.getUserMonthRankingRedisKey(String.valueOf(HostelRankingUtils.getNowNumberMonth(System.currentTimeMillis())));
            String weekKey = RedisKeyCenter.getUserWeekRankingRedisKey(String.valueOf(HostelRankingUtils.getNowNumberWeek(System.currentTimeMillis())));
            ZSetOperations zSet = redisService.getRedisTemplate().opsForZSet();
            zSet.incrementScore(overAllKey,userId,score);
            zSet.incrementScore(monthKey,userId,score);
            zSet.incrementScore(weekKey,userId,score);
            logger.info("userId: "+userId+", ?????????????????????: "+score.toString()+",??????????????????: "+zSet.score(overAllKey,userId).toString());
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
