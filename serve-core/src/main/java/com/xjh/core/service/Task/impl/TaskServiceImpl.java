package com.xjh.core.service.Task.impl;

import com.github.pagehelper.PageHelper;
import com.xjh.common.bean.Task;
import com.xjh.common.enums.PlanTypeEnum;
import com.xjh.common.enums.QueryPlanEnum;
import com.xjh.common.enums.TaskStatusEnum;
import com.xjh.common.po.PlanTaskPO;
import com.xjh.common.utils.Page;
import com.xjh.common.vo.RequestTaskVO;
import com.xjh.core.mapper.TaskMapper;
import com.xjh.core.service.Task.TaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TaskServiceImpl implements TaskService {
    @Resource
    private TaskMapper taskMapper;

    @Override
    public List<PlanTaskPO> getTaskByPlanId(RequestTaskVO taskVO) {
        try {
            List<PlanTaskPO> res = null;
            QueryPlanEnum queryType = taskVO.getQueryType();
            if (queryType == QueryPlanEnum.ALIVE_PLAN) {
                res = taskMapper.queryAlivePlan(taskVO, new Date());
            } else if (queryType == QueryPlanEnum.HISTORY_PLAN) {
                res = taskMapper.queryHistoryPlan(taskVO, new Date());
            }
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Boolean deletePlan(Integer id) {
        try {
            taskMapper.deletePlanAndTask(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean postPlan(PlanTaskPO planTaskPO) {
        List<Task> tasks = planTaskPO.getTasks();
        planTaskPO.setCreateTime(new Date());
        addEndTimeByType(planTaskPO);
        taskMapper.insertPlan(planTaskPO);
        Integer planId = planTaskPO.getId();
        tasks.stream().forEach(t -> {
            t.setPlanId(planId);
            t.setCreateTime(new Date());
        });
        taskMapper.insertTask(tasks);
        return true;
    }

    @Override
    public Boolean updatePlanAndTask(PlanTaskPO planTaskPO) {
        if (planTaskPO.getType() == PlanTypeEnum.TODAY_PLAN) {
            planTaskPO.setTimeLen(1);
        } else if (planTaskPO.getType() == PlanTypeEnum.LONG_PLAN) {
            planTaskPO.setTimeLen(-1);
        }
        taskMapper.updatePlan(planTaskPO);
        List<Task> tasks = planTaskPO.getTasks();
        tasks.stream().forEach(t -> {
            t.setCreateTime(new Date());
        });
        taskMapper.deleteTask(planTaskPO.getId());
        taskMapper.insertTask(tasks);
        return true;
    }

    @Override
    public Boolean updateStatus(RequestTaskVO taskVO) {
        try {
            if (taskVO.getStatus() == TaskStatusEnum.UN_FINISH_TASK) {
                taskVO.setStatus(TaskStatusEnum.FINISH_TASK);
            } else {
                taskVO.setStatus(TaskStatusEnum.UN_FINISH_TASK);
            }
            taskMapper.updateStatus(taskVO);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private void addEndTimeByType(PlanTaskPO plan) {
        PlanTypeEnum type = plan.getType();
        Integer timeLen = plan.getTimeLen();
        Calendar calendar = Calendar.getInstance();
        if (type == PlanTypeEnum.TODAY_PLAN) {
            Long millis = calendar.getTimeInMillis();
            Long stamp = (millis - millis % 86400000) + 57600000; //86400000 一天的毫秒值
            plan.setEndTime(new Date(stamp));
            plan.setTimeLen(1);
        } else if (type == PlanTypeEnum.COUNTDOWN_PLAN) {
            calendar.add(Calendar.DAY_OF_YEAR, timeLen);
            plan.setEndTime(calendar.getTime());
        } else {
            plan.setTimeLen(-1);
        }
    }

}
