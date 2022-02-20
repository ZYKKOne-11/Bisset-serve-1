package com.xjh.common.po;

import lombok.Data;

@Data
public class UserPO {
    private Integer id;
    private Long userId;
    private String account;
    private String name;
    private String password;
    private String email;
    private String img;
}
