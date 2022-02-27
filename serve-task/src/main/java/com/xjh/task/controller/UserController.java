package com.xjh.task.controller;

import com.xjh.common.exception.CommonException;
import com.xjh.common.model.ResultModel;
import com.xjh.common.po.UserPO;
import com.xjh.core.service.user.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;


    @PostMapping("/register")
    public ResultModel<Boolean> register(@RequestBody UserPO userInfo, @RequestBody String code) {
        try {
            Boolean res = userService.register(userInfo, code);
            return ResultModel.success(res);
        } catch (CommonException e) {
            return ResultModel.fail(e);
        }
    }

    @PostMapping("/send-email")
    public ResultModel<Boolean> sendEmail(@RequestParam("email") String emailName) {
        try {
            Boolean res = userService.sendEmail(emailName);
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
    public ResultModel<Boolean> changePassword(String password) {
        try {
            Boolean res = userService.changePassword(password);
            return ResultModel.success(res);
        } catch (CommonException e) {
            return ResultModel.fail(e);
        }
    }

    @PostMapping("/change-email")
    public ResultModel<Boolean> changeEmail(@RequestBody UserPO userInfo) {
        try {
            Boolean res = userService.changeUserEmail(userInfo);
            return ResultModel.success(res);
        } catch (CommonException e) {
            return ResultModel.fail(e);
        }
    }
}
