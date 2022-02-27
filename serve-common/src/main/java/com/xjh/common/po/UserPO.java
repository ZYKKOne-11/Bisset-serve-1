package com.xjh.common.po;

import lombok.Data;

@Data
public class UserPO {
    private Long userId;
    private String name;
    private String password;
    private String email;
    private String img;
    private String tag;
}
