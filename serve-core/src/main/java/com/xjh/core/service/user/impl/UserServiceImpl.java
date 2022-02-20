package com.xjh.core.service.user.impl;

import com.xjh.common.exception.CommonErrorCode;
import com.xjh.common.exception.CommonException;
import com.xjh.common.po.UserPO;
import com.xjh.common.utils.security.DigestUtil;
import com.xjh.core.config.ApplicationConstant;
import com.xjh.core.interceptor.token.SecurityUtils;
import com.xjh.core.interceptor.token.TokenUtil;
import com.xjh.core.mapper.UserMapper;
import com.xjh.core.service.redis.RedisService;
import com.xjh.core.service.user.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Resource
    RedisService redisService;

    @Override
    public Boolean register(UserPO userInfo) {
        Integer count = userMapper.selectAccountCount(userInfo);
        if (count > 0) {
            throw new CommonException(CommonErrorCode.VALIDATE_ERROR, "该用户名已存在");
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
    public Boolean changePassword(UserPO userInfo) {
        return null;
    }

    @Override
    public Boolean changeUserEmail(UserPO userInfo) {
        return null;
    }
}
