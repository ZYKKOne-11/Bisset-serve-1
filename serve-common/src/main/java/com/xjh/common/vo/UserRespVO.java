package com.xjh.common.vo;

import lombok.Data;

@Data
public class UserRespVO {
    /*User 表字段*/
    private Long userId;
    private String name;
    private String password;
    private String email;
    private String img;
    private String tag;
    private String sign;

    /*展示字段*/
    private Integer planNumber;
    private Integer shareNumber;
    private Integer loginNumber;
}
