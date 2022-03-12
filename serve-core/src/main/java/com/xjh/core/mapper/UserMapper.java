package com.xjh.core.mapper;

import com.xjh.common.po.UserPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    UserPO selectUserByEmail(@Param("email") String account);

    UserPO selectUserByName(@Param("name") String account);

    Integer selectAccountCountByEmail(@Param("email") String account);

    Integer selectAccountCountByName(@Param("name") String account);

    Integer selectAccountCountByNameEmail(@Param("name") String name,@Param("email") String email);

    Long insertUser(UserPO userInfo);

    void updatePasswordById(@Param("userId") Long userId, @Param("password") String digestPassword);


    List<UserPO> selectUserList(List<Long> ids);
}
