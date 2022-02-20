package com.xjh.task.controller;

import com.xjh.common.exception.CommonException;
import com.xjh.common.model.ResultModel;
import com.xjh.common.po.UserPO;
import com.xjh.core.service.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;


    @PostMapping("/register")
    public ResultModel<Boolean> register(@RequestBody UserPO userInfo) {
        try {
            Boolean res = userService.register(userInfo);
            return ResultModel.success(res);
        } catch (CommonException e) {
            return ResultModel.fail(e);
        }
    }

    @PostMapping("/login")
    public ResultModel<UserPO> login(@RequestBody UserPO userInfo, HttpServletRequest request, HttpServletResponse response) {
        try {
            UserPO res = userService.login(userInfo, request, response);
            return ResultModel.success(res);
        } catch (CommonException e) {
            return ResultModel.fail(e);
        }
    }

    @PostMapping("/changePassword")
    public ResultModel<Boolean> changePassword(@RequestBody UserPO userInfo) {
        try {
            Boolean res = userService.changePassword(userInfo);
            return ResultModel.success(res);
        } catch (CommonException e) {
            return ResultModel.fail(e);
        }
    }
}
