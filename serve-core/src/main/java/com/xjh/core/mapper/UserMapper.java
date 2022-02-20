package com.xjh.core.mapper;

import com.xjh.common.po.UserPO;

public interface UserMapper {
    UserPO selectUserByAccount(UserPO userInfo);

    Integer selectAccountCount(UserPO userInfo);

    void insertUser(UserPO userInfo);
}
