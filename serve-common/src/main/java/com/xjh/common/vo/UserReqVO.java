package com.xjh.common.vo;

import com.xjh.common.enums.UserChangeReqTypeEnum;
import com.xjh.common.po.UserPO;
import lombok.Data;

import java.util.List;

@Data
public class UserReqVO {
    private String account;
    private UserPO user;
    private String code;
    private String email;
    private UserChangeReqTypeEnum reqType;
    private String oldPassword;
    private String newPassword;
    private String newName;
    private String newEmail;
    private List<String> tag;
}
