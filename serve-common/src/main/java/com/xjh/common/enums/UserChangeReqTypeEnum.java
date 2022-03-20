package com.xjh.common.enums;

import com.xjh.common.enums.basic.BaseCodeEnum;

public enum  UserChangeReqTypeEnum implements BaseCodeEnum {

    CHANGE_USER_UNSAFE(-1,"操作未知"),
    CHANGE_USER_NAME(0,"修改名称"),
    CHANGE_USER_PASSWORD(1,"修改密码"),
    CHANGE_USER_EMAIL(2,"修改邮箱"),
    CHANGE_USER_TAG(3,"修改标签");

    Integer code;
    String desc;


    private UserChangeReqTypeEnum(Integer code, String desc) {
        this.desc = desc;
        this.code = code;
    }


    @Override
    public int getCode() {
        return code;
    }

    public String getDesc(){
        return desc;
    }
}
