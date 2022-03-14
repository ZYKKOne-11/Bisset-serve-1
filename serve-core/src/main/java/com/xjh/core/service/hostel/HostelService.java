package com.xjh.core.service.hostel;

import com.xjh.common.enums.RankingTypeEnum;
import com.xjh.common.po.UserDiscussPO;
import com.xjh.common.po.UserPO;
import com.xjh.common.po.UserSharePO;
import com.xjh.common.utils.Page;
import com.xjh.common.vo.HostelVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface HostelService {
    Map<RankingTypeEnum,List<UserPO>> queryRanking();

    Boolean userShare(HostelVO hostelVO, HttpServletRequest request);

    Page<UserSharePO> selectUserShareInfo(HostelVO hostelVO);

    Page<UserDiscussPO> selectUserDiscussInfo(HostelVO hostelVO);

    Boolean publishDiscussByShare(HostelVO hostelVO);
}
