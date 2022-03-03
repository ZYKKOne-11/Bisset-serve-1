package com.xjh.common.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xjh.common.bean.Task;
import com.xjh.common.enums.PlanTypeEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PlanTaskPO {
    //计划Id
    Integer id;

    //计划名称
    String name;

    //计划类型
    PlanTypeEnum type;

    //创建计划Id
    Long userId;

    //计划执行时间
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    //计划结束时间
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    Date endTime;

    //当前计划下的任务List
    List<Task> tasks;
}
