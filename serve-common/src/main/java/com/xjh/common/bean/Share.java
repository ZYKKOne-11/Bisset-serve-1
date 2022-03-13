package com.xjh.common.bean;

import lombok.Data;

//用户思维导图分享PO
@Data
public class Share {
    private Long id;
    private String title;
    private String details;
    private String img;
    private Integer isDiscuss;
    private Integer isReviewed;
}
