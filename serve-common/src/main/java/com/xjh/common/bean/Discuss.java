package com.xjh.common.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

//用户登录表
@Data
public class Discuss {
    Long id;

    String detail;

    Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    Date createTime;
}
