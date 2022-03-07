package com.xjh.common.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xjh.common.enums.PlanTypeEnum;
import com.xjh.common.enums.QueryPlanEnum;
import com.xjh.common.enums.TaskStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestTaskVO {
    @JsonProperty("pId")
    Integer pId;

    @JsonProperty("tId")
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
