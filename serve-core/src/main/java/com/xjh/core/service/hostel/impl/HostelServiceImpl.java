package com.xjh.core.service.hostel.impl;

import com.xjh.common.consts.HostelListConst;
import com.xjh.common.enums.RankingTypeEnum;
import com.xjh.common.exception.CommonErrorCode;
import com.xjh.common.exception.CommonException;
import com.xjh.common.po.UserPO;
import com.xjh.core.mapper.HostelMapper;
import com.xjh.core.service.hostel.HostelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HostelServiceImpl implements HostelService {

    @Resource
    private HostelMapper hostelMapper;

    @Resource
    private HostelListConst hostelListConst;

    @Override
    public Map<RankingTypeEnum,List<UserPO>> queryRanking() {
        try {
            Map<RankingTypeEnum,List<UserPO>> res = new HashMap<>();
            res.put(RankingTypeEnum.OVER_ALL_RANK,hostelListConst.getOverAllRankingList());
            res.put(RankingTypeEnum.MONTH_RANK,hostelListConst.getMonthRankingList());
            res.put(RankingTypeEnum.WEEK_RANK,hostelListConst.getWeekRankingList());
            return res;
        }catch (Exception e){
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR,"用户排行榜查询异常，请稍后重试");
        }
    }
}
