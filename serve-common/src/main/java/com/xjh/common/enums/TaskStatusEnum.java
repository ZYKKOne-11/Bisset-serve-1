package com.xjh.common.enums;

import com.xjh.common.enums.basic.BaseCodeEnum;
import lombok.Data;


public enum TaskStatusEnum implements BaseCodeEnum {

    UN_FINISH_TASK(0, "未完成"),
    FINISH_TASK(1, "已完成");

    Integer code;
    String status;


    private TaskStatusEnum(Integer code, String status) {
        this.status = status;
        this.code = code;
    }


    @Override
    public int getCode() {
        return code;
    }
}
