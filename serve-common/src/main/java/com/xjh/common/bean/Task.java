package com.xjh.common.bean;

import com.xjh.common.enums.TaskStatusEnum;
import lombok.Data;

import java.util.Date;

@Data
public class Task {
    //ID
    private Integer id;

    //任务名称
    private String name;

    //任务内容
    private String content;

    //所属计划Id
    private Integer planId;

    //创建时间
    private Date createTime;

    //当前任务状态
    private TaskStatusEnum status;
}
