package com.xjh.core.service.user;

import com.xjh.common.po.UserPO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
    /**
     * 注册
     *
     * @param userInfo 注册信息
     * @return 是否成功
     */
    Boolean register(UserPO userInfo);

    /**
     * 登录
     *
     * @param userInfo 登录信息
     * @return 令牌
     */
    UserPO login(UserPO userInfo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 更改密码
     *
     * @param userInfo 用户信息
     * @return 是否成功
     */
    Boolean changePassword(UserPO userInfo);

    /**
     * 更改用户邮箱
     *
     * @param userInfo 用户信息
     * @return 是否成功
     */
    Boolean changeUserEmail(UserPO userInfo);
}