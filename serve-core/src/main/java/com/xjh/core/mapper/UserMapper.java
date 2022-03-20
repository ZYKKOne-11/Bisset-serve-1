package com.xjh.core.mapper;

import com.xjh.common.po.UserPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserMapper {
    UserPO selectUserByEmail(@Param("email") String account);

    UserPO selectUserByName(@Param("name") String account);

    Integer selectAccountCountByEmail(@Param("email") String account);

    Integer selectAccountCountByName(@Param("name") String account);

    Integer selectAccountCountByNameEmail(@Param("name") String name,@Param("email") String email);

    Long insertUser(UserPO userInfo);

    void updatePasswordById(@Param("userId") Long userId, @Param("password") String digestPassword);

    void updateEmailById(@Param("userId") Long userId, @Param("email") String email);

    void updateNameById(@Param("userId") Long userId, @Param("name") String newName);

    void updateImgById(@Param("userId") Long userId, @Param("imgPath") String uploadFilePath);

    void updateTagById(@Param("userId") Long userId, @Param("tag") String toString);


    List<UserPO> selectUserList(List<Long> ids);
}
