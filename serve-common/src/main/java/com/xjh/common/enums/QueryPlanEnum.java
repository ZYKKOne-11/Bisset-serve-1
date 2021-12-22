package com.xjh.common.enums;

import com.xjh.common.enums.basic.BaseCodeEnum;

public enum QueryPlanEnum implements BaseCodeEnum {

    ALIVE_PLAN(0, "进行中的计划"),
    HISTORY_PLAN(1, "历史计划");

    Integer code;
    String desc;

    private QueryPlanEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }
}
