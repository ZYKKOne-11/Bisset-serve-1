package com.xjh.common.po;

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

    //计划时常
    Integer timeLen;

    //创建计划Id
    Long userId;

    //计划创建时间
    Date createTime;

    //计划结束时间
    Date endTime;

    //当前计划下的任务List
    List<Task> tasks;
}
