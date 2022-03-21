package com.xjh.core.service.user;

import com.xjh.common.po.UserPO;
import com.xjh.common.vo.UserReqVO;
import com.xjh.common.vo.UserRespVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserService {
    /**
     * 注册
     *
     * @param userInfo 注册信息
     * @return 是否成功
     */
    Boolean register(UserReqVO userInfo);

    /**
     * 注册参数合理性校验
     * @param userReqVO email name 校验
     * @return
     */
    Boolean checkRegisterParam(UserReqVO userReqVO);
    /**
     * 登录
     *
     * @param userInfo 登录信息
     * @return 令牌
     */
    UserPO login(UserReqVO userInfo, HttpServletRequest request, HttpServletResponse response);

    Boolean sendEmail(String emailName);

    UserRespVO query();

    List<UserPO> selectUserList(List<Long> ids);

    Boolean logout(HttpServletRequest request);

    Boolean change(UserReqVO userReqVO, HttpServletRequest req);

    String uploadImg(HttpServletRequest request);

    Boolean update(UserReqVO user, HttpServletRequest request);
}
