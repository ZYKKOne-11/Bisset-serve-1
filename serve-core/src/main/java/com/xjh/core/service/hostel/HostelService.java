package com.xjh.core.service.hostel;

import com.xjh.common.enums.RankingTypeEnum;
import com.xjh.common.po.UserPO;

import java.util.List;
import java.util.Map;

public interface HostelService {
    Map<RankingTypeEnum,List<UserPO>> queryRanking();
}
