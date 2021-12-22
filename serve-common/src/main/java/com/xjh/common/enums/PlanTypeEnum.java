package com.xjh.common.enums;

import com.xjh.common.enums.basic.BaseCodeEnum;
import lombok.Data;


public enum PlanTypeEnum implements BaseCodeEnum {

    TODAY_PLAN(0, "今日计划"),
    LONG_PLAN(1, "长期计划"),
    COUNTDOWN_PLAN(2, "倒计时计划");

    Integer code;
    String type;


    private PlanTypeEnum(Integer code, String type) {
        this.type = type;
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }
}
