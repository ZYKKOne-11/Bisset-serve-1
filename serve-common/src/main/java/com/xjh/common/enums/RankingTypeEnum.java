package com.xjh.common.enums;

import com.xjh.common.enums.basic.BaseCodeEnum;

public enum  RankingTypeEnum implements BaseCodeEnum {
    OVER_ALL_RANK(0,"总排行榜"),
    MONTH_RANK(1,"月排行榜"),
    WEEK_RANK(2,"周排行榜");

    Integer code;
    String desc;

    RankingTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }
}
