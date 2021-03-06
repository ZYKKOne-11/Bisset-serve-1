package com.xjh.core.service.Task.impl;

import com.github.pagehelper.PageHelper;
import com.xjh.common.bean.Hotspot;
import com.xjh.common.bean.Task;
import com.xjh.common.enums.PlanTypeEnum;
import com.xjh.common.enums.QueryPlanEnum;
import com.xjh.common.enums.TaskStatusEnum;
import com.xjh.common.exception.CommonErrorCode;
import com.xjh.common.exception.CommonException;
import com.xjh.common.po.PlanTaskPO;
import com.xjh.common.utils.Page;
import com.xjh.common.vo.RequestTaskVO;
import com.xjh.core.interceptor.token.SecurityUtils;
import com.xjh.core.mapper.HotspotMapper;
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

    @Resource
    private HotspotMapper hotspotMapper;

    @Override
    public List<PlanTaskPO> getTaskByPlanId(RequestTaskVO taskVO) {
        try {
            List<PlanTaskPO> res = null;
            taskVO.setUserId(SecurityUtils.getUserId());
            QueryPlanEnum queryType = taskVO.getQueryType();
            if (queryType == QueryPlanEnum.ALIVE_PLAN) {
                res = taskMapper.queryAlivePlan(taskVO, new Date());
            } else if (queryType == QueryPlanEnum.HISTORY_PLAN) {
                res = taskMapper.queryHistoryPlan(taskVO, new Date());
            }
            return res;
        } catch (Exception e) {
            throw new CommonException(CommonErrorCode.SERVER_POWER_LESS, "查询数据异常，请稍后重试");
        }
    }

    @Override
    public Boolean deletePlan(Integer id) {
        try {
            Long userId = SecurityUtils.getUserId();
            taskMapper.deletePlanAndTask(id, userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean postPlan(PlanTaskPO planTaskPO) {
        try {
            List<Task> tasks = planTaskPO.getTasks();
            if (planTaskPO.getCreateTime() == null) {
                planTaskPO.setCreateTime(new Date());
            }
            planTaskPO.setUserId(SecurityUtils.getUserId());
            addEndTimeByType(planTaskPO);
            taskMapper.insertPlan(planTaskPO);
            Integer planId = planTaskPO.getId();
            tasks.stream().forEach(t -> {
                t.setPlanId(planId);
                t.setCreateTime(new Date());
                t.setStatus(TaskStatusEnum.UN_FINISH_TASK);
            });
            taskMapper.insertTask(tasks);
            return true;
        } catch (Exception e) {
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "新建计划失败");
        }
    }

    @Override
    public Boolean updatePlanAndTask(PlanTaskPO planTaskPO) {
        try {
            planTaskPO.setUserId(SecurityUtils.getUserId());
            taskMapper.updatePlan(planTaskPO);
            List<Task> tasks = planTaskPO.getTasks();
            tasks.stream().forEach(t -> {
                if (t.getCreateTime() == null){
                    t.setCreateTime(new Date());
                }
                t.setPlanId(planTaskPO.getId());
            });
            taskMapper.deleteTask(planTaskPO.getId());
            taskMapper.insertTask(tasks);
            return true;
        } catch (Exception e) {
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "修改任务失败，请稍后重试");
        }
    }

    @Override
    public Boolean updateStatus(RequestTaskVO taskVO) {
        try {
            if (taskVO.getStatus() == TaskStatusEnum.UN_FINISH_TASK) {
                taskVO.setStatus(TaskStatusEnum.FINISH_TASK);
            } else {
                taskVO.setStatus(TaskStatusEnum.UN_FINISH_TASK);
            }
            Long userId = SecurityUtils.getUserId();
            taskVO.setUserId(userId);
            taskMapper.updateStatus(taskVO);
            return true;
        } catch (Exception e) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, "修改任务状态异常");
        }
    }

    @Override
    public List<Hotspot> queryHotspot() {
        try {
            List<Hotspot> hotspot = hotspotMapper.queryHotspot();
            return hotspot;
        } catch (Exception e) {
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR, "查询热点信息异常");
        }
    }


    private void addEndTimeByType(PlanTaskPO plan) {
        PlanTypeEnum type = plan.getType();
        Calendar calendar = Calendar.getInstance();
        if (type == PlanTypeEnum.TODAY_PLAN) {
            Long millis = calendar.getTimeInMillis();
            Long stamp = (millis - millis % 86400000) + 57600000; //86400000 一天的毫秒值
            plan.setEndTime(new Date(stamp));
        } else {
            if (plan.getEndTime() == null) {
                throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, "非今日计划请设置结束时间");
            }
        }
    }


}
