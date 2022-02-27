package com.xjh.common.vo;

import com.xjh.common.po.UserPO;
import lombok.Data;

@Data
public class UserVO {
    private String account;
    private UserPO user;
    private String code;
    private String email;
    private String oldPassword;
    private String newPassword;
}
