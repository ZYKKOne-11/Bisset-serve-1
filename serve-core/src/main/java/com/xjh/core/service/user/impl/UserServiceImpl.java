package com.xjh.core.service.user.impl;

import com.xjh.common.bean.Share;
import com.xjh.common.consts.HostelConstant;
import com.xjh.common.enums.UserChangeReqTypeEnum;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
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

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
    private String  uploadPath = PropertyLoader.getProperty("user.img.upload.path");
    private static final Long UPLOAD_FILE_MAX_SIZE = PropertyLoader.getLongProperty("upload.file.max.size");
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
            checkEmailCode(userInfo);
            userPO.setPassword(Base64.encodeBase64String(userPO.getPassword().getBytes()));
            userMapper.insertUser(userPO);
            userExecAddScore(String.valueOf(userPO.getUserId()), HostelConstant.USER_REGISTER_SCORE);
            return true;
        } catch (Exception e) {
            logger.error("用户注册失败，User：" + userInfo.toString());
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, e.getMessage());
        }
    }

    @Override
    public Boolean checkRegisterParam(UserVO userVO) {
        try {
            if (userVO.getUser().getEmail() != null && !userVO.getUser().getEmail().equals("")) {
                Integer count = userMapper.selectAccountCountByEmail(userVO.getUser().getEmail());
                if (count > 0) {
                    logger.debug(DFLAG_USER_LOG + ",登录参数校验，邮箱已存在，email: " + userVO.getEmail());
                    return false;
                }
            }
            if (userVO.getUser().getName() != null && !userVO.getUser().getName().equals("")) {
                Integer count = userMapper.selectAccountCountByName(userVO.getUser().getName());
                if (count > 0) {
                    logger.debug(DFLAG_USER_LOG + ",登录参数校验，用户昵称已存在，name: " + userVO.getUser().getName());
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            //降级处理
            logger.error("用户名称或邮箱校验异常");
            return false;
        }
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
        userExecAddScore(String.valueOf(userPO.getUserId()), HostelConstant.USER_REGISTER_SCORE);
        return userPO;
    }

    @Override
    public Boolean logout(HttpServletRequest request) {
        try {
            String userToken = TokenUtil.checkAndReturnToken(request);
            if (userToken != null) {
                String redisKey = RedisKeyCenter.getUserLoginInfoRedisKey(userToken);
                redisService.delete(redisKey);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Id: " + SecurityUtils.getUserId() + "的用户退出登录失败，异常信息：" + e.getMessage());
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "退出登录异常，请稍后重试");
        }
    }

    @Override
    public Boolean change(UserVO userVO, HttpServletRequest req) {
        try{
            UserPO user = SecurityUtils.getUserInfo();
            userVO.setUser(user);
            checkChangeParam(userVO);
            switch (userVO.getReqType()){
                case CHANGE_USER_NAME:
                    changeUserName(userVO,req);
                    break;
                case CHANGE_USER_PASSWORD:
                    changePassword(userVO,req);
                    break;
                case CHANGE_USER_EMAIL:
                    changeUserEmail(userVO,req);
                    break;
                case CHANGE_USER_TAG:
                    changeUserTag(userVO,req);
                    break;
                default:
                    return false;
            }
            return true;
        }catch (Exception e){
            logger.error("修改用户信息失败，userVo="+userVO.toString());
            throw new CommonException(CommonErrorCode.SERVER_POWER_LESS,"修改失败，操作类型为："+userVO.getReqType().getDesc());
        }
    }

    @Override
    public Boolean uploadImg(HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartHttpServletRequest=(MultipartHttpServletRequest)(request);
            MultipartFile uploadFile=multipartHttpServletRequest.getFile("img");
            if (uploadFile.isEmpty()){
                throw new CommonException(CommonErrorCode.VALIDATE_ERROR,"上传的文件为空");
            }
            String uploadFilePath = uploadFiles(uploadFile, request);
            UserPO user = SecurityUtils.getUserInfo();
            user.setImg(uploadFilePath);
            userMapper.updateImgById(user.getUserId(),uploadFilePath);
            String token = TokenUtil.getToken(request);
            String userLoginInfoRedisKey = RedisKeyCenter.getUserLoginInfoRedisKey(token);
            redisService.set(userLoginInfoRedisKey, user, (long) SecurityUtils.TOKEN_TTL_TIME, TimeUnit.SECONDS);
            return true;
        }catch (Exception e){
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR,"上传用户头像失败，请稍后重试");
        }
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
        try {
            List<UserPO> res = userMapper.selectUserList(ids);
            return res;
        } catch (Exception e) {
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "查询User_List异常，请稍后再试");
        }
    }

    /**
     * 用户修改操作入参合法性校验
     * @param userVO
     */
    private void checkChangeParam(UserVO userVO){
        if (userVO.getReqType() == null){
            userVO.setReqType(UserChangeReqTypeEnum.CHANGE_USER_UNSAFE);
            throw new CommonException(CommonErrorCode.VALIDATE_ERROR,"入参操作类型不可为空");
        }
        switch (userVO.getReqType()){
            case CHANGE_USER_NAME:
                Integer nameCount = userMapper.selectAccountCountByName(userVO.getNewName());
                if (nameCount == null || nameCount != 0){
                    throw new CommonException(CommonErrorCode.VALIDATE_ERROR,"您修改的名称已存在");
                }
                break;
            case CHANGE_USER_PASSWORD:
                checkEmailCode(userVO);
                String originPassword = userVO.getUser().getPassword();
                String decodePassword = StringUtils.newStringUsAscii(Base64.decodeBase64(originPassword));
                if (!decodePassword.equals(userVO.getOldPassword())) {
                    throw new CommonException(CommonErrorCode.VALIDATE_ERROR, "旧密码不正确，请重新输入");
                }
                break;
            case CHANGE_USER_EMAIL:
                userVO.getUser().setEmail(userVO.getNewEmail());
                checkEmailCode(userVO);
                Integer emailCount = userMapper.selectAccountCountByEmail(userVO.getEmail());
                if (emailCount == null || emailCount != 0){
                    throw new CommonException(CommonErrorCode.VALIDATE_ERROR,"您修改的邮箱已存在");
                }

                if (!isEmail(userVO.getNewEmail())){
                    throw new CommonException(CommonErrorCode.VALIDATE_ERROR,"新的邮箱格式不合法");
                }
                break;
            default:
                throw new CommonException(CommonErrorCode.VALIDATE_ERROR,"数据参数校验异常");
        }
    }
    private Boolean changePassword(UserVO userVO, HttpServletRequest req) {
        UserPO userInfo = userVO.getUser();
        Long userId = SecurityUtils.getUserId();
        String newPassword = Base64.encodeBase64String(userVO.getNewPassword().getBytes());
        userInfo.setPassword(newPassword);
        userMapper.updatePasswordById(userId, newPassword);
        String token = TokenUtil.getToken(req);
        String userLoginInfoRedisKey = RedisKeyCenter.getUserLoginInfoRedisKey(token);
        redisService.set(userLoginInfoRedisKey, userInfo, (long) SecurityUtils.TOKEN_TTL_TIME, TimeUnit.SECONDS);
        return true;
    }

    private Boolean changeUserEmail(UserVO userVO, HttpServletRequest req) {
        UserPO userInfo = userVO.getUser();
        Long userId = SecurityUtils.getUserId();
        userInfo.setEmail(userVO.getNewEmail());
        userMapper.updateEmailById(userId, userVO.getNewEmail());
        String token = TokenUtil.getToken(req);
        String userLoginInfoRedisKey = RedisKeyCenter.getUserLoginInfoRedisKey(token);
        redisService.set(userLoginInfoRedisKey, userInfo, (long) SecurityUtils.TOKEN_TTL_TIME, TimeUnit.SECONDS);
        return true;
    }

    private Boolean changeUserName(UserVO userVO, HttpServletRequest req){
        UserPO userInfo = userVO.getUser();
        Long userId = SecurityUtils.getUserId();
        userInfo.setName(userVO.getNewName());
        userMapper.updateNameById(userId, userVO.getNewName());
        String token = TokenUtil.getToken(req);
        String userLoginInfoRedisKey = RedisKeyCenter.getUserLoginInfoRedisKey(token);
        redisService.set(userLoginInfoRedisKey, userInfo, (long) SecurityUtils.TOKEN_TTL_TIME, TimeUnit.SECONDS);
        return true;
    }

    private Boolean changeUserTag(UserVO userVO,HttpServletRequest req){
        UserPO userInfo = userVO.getUser();
        Long userId = SecurityUtils.getUserId();
        userInfo.setTag(userVO.getTag().toString());
        userMapper.updateTagById(userId, userVO.getTag().toString());
        String token = TokenUtil.getToken(req);
        String userLoginInfoRedisKey = RedisKeyCenter.getUserLoginInfoRedisKey(token);
        redisService.set(userLoginInfoRedisKey, userInfo, (long) SecurityUtils.TOKEN_TTL_TIME, TimeUnit.SECONDS);
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

    private void checkEmailCode(UserVO userInfo){
        String token = Base64.encodeBase64String(userInfo.getUser().getEmail().getBytes());
        String emailRedisKey = RedisKeyCenter.getUserEmailInfoRedisKey(token);
        String emailCode = redisService.get(emailRedisKey);
        if (emailCode == null || !emailCode.equals(userInfo.getCode())) {
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "邮箱验证码错误或失效，请再试一次");
        }
    }

    /**
     * 邮箱时效性验证
     * @param emailName
     * @return
     */
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

    /**
     * 用户积分增加操作
     * @param userId
     * @param score
     * @return
     */
    private Boolean userExecAddScore(String userId, Double score) {
        try {
            String overAllKey = RedisKeyCenter.getUserOverAllRankingRedisKey();
            String monthKey = RedisKeyCenter.getUserMonthRankingRedisKey(String.valueOf(HostelRankingUtils.getNowNumberMonth(System.currentTimeMillis())));
            String weekKey = RedisKeyCenter.getUserWeekRankingRedisKey(String.valueOf(HostelRankingUtils.getNowNumberWeek(System.currentTimeMillis())));
            ZSetOperations zSet = redisService.getRedisTemplate().opsForZSet();
            zSet.incrementScore(overAllKey, userId, score);
            zSet.incrementScore(monthKey, userId, score);
            zSet.incrementScore(weekKey, userId, score);
            logger.info("userId: " + userId + ", 排行榜积分增加: " + score.toString() + ",当前总分数为: " + zSet.score(overAllKey, userId).toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 上传用户头像文件
     * @param uploadFile
     * @param request
     * @return
     * @throws Exception
     */
    public String uploadFiles(MultipartFile uploadFile, HttpServletRequest request) throws Exception{

        // 对上传的文件重命名，避免文件重名
        String oldName = uploadFile.getOriginalFilename();
        String newName = UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf("."));

        if (newName.contains(";")){
            newName = newName.substring(0, newName.lastIndexOf(";"));
        }

        //如果名称为空，返回一个文件名为空的错误
        if (org.apache.commons.lang3.StringUtils.isEmpty(oldName)){
            throw  new CommonException(CommonErrorCode.VALIDATE_ERROR,"上传的文件损坏，无法更新头像");
        }
        //如果文件超过最大值，返回超出可上传最大值的错误
        if (uploadFile.getSize()/(1024*1024)>UPLOAD_FILE_MAX_SIZE){
            throw  new CommonException(CommonErrorCode.VALIDATE_ERROR,"图片过大，无法上传");
        }
        String format = sdf.format(new Date());
        File folder = new File(uploadPath + format);

        //目录不存在则创建目录
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }
        logger.info("图片地址："+newName);
        // 文件保存
        uploadFile.transferTo(new File(folder, newName));
        // 返回上传文件的访问路径
        String filePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()+ uploadPath  + format + newName;
        logger.info("上传文件的访问路径 filePath= "+filePath);
        return filePath;
    }
}
