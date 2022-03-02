package com.xjh.common.vo;

import com.xjh.common.enums.PlanTypeEnum;
import com.xjh.common.enums.QueryPlanEnum;
import com.xjh.common.enums.TaskStatusEnum;
import lombok.Data;

@Data
public class RequestTaskVO {
    Integer pId;

    Integer tId;

    Long userId;

    String planName;

    TaskStatusEnum status;

    PlanTypeEnum type;

    //查询计划类型
    QueryPlanEnum queryType;

    //时间查询字段
    String queryTime;

    //分页所需字段
    Integer number;

    Integer size;
}
