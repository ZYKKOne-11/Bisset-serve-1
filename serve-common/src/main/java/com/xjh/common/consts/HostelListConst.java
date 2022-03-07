package com.xjh.common.consts;

import com.xjh.common.po.UserPO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HostelListConst {

    //总排行榜
    private List<UserPO> overAllRankingList = new ArrayList<>();

    //月排行榜
    private List<UserPO> monthRankingList = new ArrayList<>();

    //周排行榜
    private List<UserPO> weekRankingList = new ArrayList<>();

}
