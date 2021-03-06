package com.xjh.task.controller;

import com.xjh.common.exception.CommonException;
import com.xjh.common.model.ResultModel;
import com.xjh.common.po.UserPO;
import com.xjh.common.vo.UserVO;
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
    public ResultModel<Boolean> register(@RequestBody UserVO userInfo) {
        try {
            Boolean res = userService.register(userInfo);
            return ResultModel.success(res);
        } catch (CommonException e) {
            return ResultModel.fail(e);
        }
    }

    @PostMapping("/check-param")
    public ResultModel<Boolean> checkRegisterParam(@RequestBody UserVO userVO){
        try{
            Boolean res = userService.checkRegisterParam(userVO);
            return ResultModel.success(res);
        }catch (CommonException e){
            return ResultModel.success(false);
        }
    }

    @PostMapping("/send-email")
    public ResultModel<Boolean> sendEmail(@RequestBody UserVO userInfo) {
        try {
            Boolean res = userService.sendEmail(userInfo.getEmail());
            return ResultModel.success(res);
        } catch (CommonException e) {
            return ResultModel.fail(e);
        }
    }

    @PostMapping("/login")
    public ResultModel<UserPO> login(@RequestBody UserVO userInfo, HttpServletRequest request, HttpServletResponse response) {
        try {
            UserPO res = userService.login(userInfo, request, response);
            return ResultModel.success(res);
        } catch (CommonException e) {
            return ResultModel.fail(e);
        }
    }

    @PostMapping("/changePassword")
    public ResultModel<Boolean> changePassword(@RequestBody UserVO userInfo, HttpServletRequest req) {
        try {
            Boolean res = userService.changePassword(userInfo.getOldPassword(), userInfo.getNewPassword(), req);
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

    @GetMapping("/query")
    public ResultModel<UserPO> query() {
        try {
            UserPO user = userService.query();
            return ResultModel.success(user);
        } catch (CommonException e) {
            return ResultModel.fail(e);
        }
    }
}
