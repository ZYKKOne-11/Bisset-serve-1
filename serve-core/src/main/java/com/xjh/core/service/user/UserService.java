package com.xjh.core.service.user;

import com.xjh.common.po.UserPO;
import com.xjh.common.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
    /**
     * 注册
     *
     * @param userInfo 注册信息
     * @return 是否成功
     */
    Boolean register(UserVO userInfo);

    /**
     * 登录
     *
     * @param userInfo 登录信息
     * @return 令牌
     */
    UserPO login(UserVO userInfo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 更改密码
     *
     * @param oldPassword 旧密码
     * @Param newPassword 新密码
     * @return 是否成功
     */
    Boolean changePassword(String oldPassword,String newPassword,HttpServletRequest req);

    /**
     * 更改用户邮箱
     *
     * @param userInfo 用户信息
     * @return 是否成功
     */
    Boolean changeUserEmail(UserPO userInfo);

    Boolean sendEmail(String emailName);
}
