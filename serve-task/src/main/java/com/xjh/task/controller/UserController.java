package com.xjh.task.controller;

import com.xjh.common.exception.CommonException;
import com.xjh.common.model.ResultModel;
import com.xjh.common.po.UserPO;
import com.xjh.common.vo.UserReqVO;
import com.xjh.common.vo.UserRespVO;
import com.xjh.core.interceptor.token.SecurityUtils;
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
    public ResultModel<Boolean> register(@RequestBody UserReqVO userInfo) {
        try {
            Boolean res = userService.register(userInfo);
            return ResultModel.success(res);
        } catch (CommonException e) {
            return ResultModel.fail(e);
        }
    }

    @PostMapping("/check-param")
    public ResultModel<Boolean> checkRegisterParam(@RequestBody UserReqVO userReqVO){
        try{
            Boolean res = userService.checkRegisterParam(userReqVO);
            return ResultModel.success(res);
        }catch (CommonException e){
            return ResultModel.success(false);
        }
    }

    @PostMapping("/send-email")
    public ResultModel<Boolean> sendEmail(@RequestBody UserReqVO userInfo) {
        try {
            Boolean res = userService.sendEmail(userInfo.getEmail());
            return ResultModel.success(res);
        } catch (CommonException e) {
            return ResultModel.fail(e);
        }
    }

    @PostMapping("/login")
    public ResultModel<UserPO> login(@RequestBody UserReqVO userInfo, HttpServletRequest request, HttpServletResponse response) {
        try {
            UserPO res = userService.login(userInfo, request, response);
            return ResultModel.success(res);
        } catch (CommonException e) {
            return ResultModel.fail(e);
        }
    }

    @GetMapping("/logout")
    public ResultModel<Boolean> logout(HttpServletRequest request){
        try {
            Boolean res = userService.logout(request);
            SecurityUtils.remove();
            return ResultModel.success(res);
        }catch (CommonException e){
            return ResultModel.fail(e);
        }
    }

    @PostMapping("/change")
    public ResultModel<Boolean> change(@RequestBody UserReqVO userReqVO, HttpServletRequest request){
        try{
            Boolean res = userService.change(userReqVO,request);
            return ResultModel.success(res);
        }catch (CommonException e){
            return ResultModel.fail(e);
        }
    }

    @PostMapping("/update")
    public ResultModel<Boolean> update(@RequestBody UserReqVO user, HttpServletRequest request){
        try {
            Boolean res = userService.update(user, request);
            return ResultModel.success(res);
        }catch (CommonException e){
            return ResultModel.fail(e);
        }
    }

    @PostMapping("/uploadImg")
    public ResultModel<String> uploadImg(HttpServletRequest request){
        try{
            String res = userService.uploadImg(request);
            return ResultModel.success(res);
        }catch (CommonException e){
            return ResultModel.fail(e);
        }
    }

    @GetMapping("/query")
    public ResultModel<UserRespVO> query() {
        try {
            UserRespVO user = userService.query();
            return ResultModel.success(user);
        } catch (CommonException e) {
            return ResultModel.fail(e);
        }
    }
}
