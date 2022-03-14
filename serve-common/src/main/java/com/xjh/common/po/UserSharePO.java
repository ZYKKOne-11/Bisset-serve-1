package com.xjh.common.po;

import com.xjh.common.bean.Share;
import lombok.Data;

@Data
public class UserSharePO {
    private Long id;

    private UserPO user;

    private Share share;
}
