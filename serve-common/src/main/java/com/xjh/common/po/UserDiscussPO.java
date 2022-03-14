package com.xjh.common.po;

import com.xjh.common.bean.Discuss;
import lombok.Data;

@Data
public class UserDiscussPO {
    private Long id;

    private UserPO user;

    private Discuss discuss;
}
