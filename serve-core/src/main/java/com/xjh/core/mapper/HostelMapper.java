package com.xjh.core.mapper;

import com.xjh.common.bean.Discuss;
import com.xjh.common.bean.Share;
import com.xjh.common.po.UserDiscussPO;
import com.xjh.common.po.UserSharePO;
import com.xjh.common.vo.HostelVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HostelMapper {
    Integer insertShare(Share share);

    Integer insertUserShare(@Param("userId") Long userId, @Param("shareId") Long shareId);

    List<UserSharePO> selectUserShareInfo(HostelVO hostelVO);

    List<UserDiscussPO> selectUserDiscussInfo(@Param("shareId") Long shareId);

    Integer insertDiscuss(Discuss discuss);

    Integer insertUserDiscuss(@Param("userId") Long userId, @Param("discussId") Long discussId);

    Integer insertShareDiscuss(@Param("shareId") Long shareId, @Param("discussId") Long discussId);

    Integer selectUserShareCount(HostelVO hostelVO);

    Integer selectUserDiscussCount(@Param("shareId")Long shareId);
}
