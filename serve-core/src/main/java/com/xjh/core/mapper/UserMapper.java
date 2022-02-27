package com.xjh.core.mapper;

import com.xjh.common.po.UserPO;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    UserPO selectUserByAccount(UserPO userInfo);

    Integer selectAccountCount(UserPO userInfo);

    void insertUser(UserPO userInfo);

    void updatePasswordById(@Param("userId") Long userId, @Param("password") String digestPassword);
}
