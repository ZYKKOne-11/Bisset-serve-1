package com.xjh.task.controller;

import com.xjh.common.exception.CommonErrorCode;
import com.xjh.common.exception.CommonException;
import com.xjh.common.model.ResultModel;
import com.xjh.common.po.PlanTaskPO;
import com.xjh.common.vo.RequestTaskVO;
import com.xjh.core.service.Task.TaskService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Resource
    private TaskService taskService;

    @GetMapping("/plan")
    public ResultModel<List<PlanTaskPO>> getTaskByPlanId(@ModelAttribute RequestTaskVO taskVO) {
        List<PlanTaskPO> res = taskService.getTaskByPlanId(taskVO);
        return ResultModel.success(res, "数据查询成功");
    }

    @PostMapping("/plan")
    public ResultModel<Boolean> postPlan(@RequestBody PlanTaskPO planTaskPO) {
        try {
            Boolean res = taskService.postPlan(planTaskPO);
            return ResultModel.success(res, "新建计划成功");
        } catch (CommonException e) {
            e.setError(CommonErrorCode.UNKNOWN_ERROR);
            e.setErrMsg("新建计划失败，操作异常");
            return ResultModel.fail(e);
        }
    }

    @PutMapping("/plan")
    public ResultModel<Boolean> putPlan(@RequestBody PlanTaskPO planTaskPO) {
        try {
            Boolean res = taskService.updatePlanAndTask(planTaskPO);
            return ResultModel.success(res, "修改成功");
        } catch (CommonException e) {
            e.setError(CommonErrorCode.REQUEST_PARAM_ERROR);
            e.setErrMsg("修改操作出现异常");
            return ResultModel.fail(e);
        }
    }

    @PutMapping("/status")
    public ResultModel<Boolean> putStatus(@RequestBody RequestTaskVO taskVO) {
        try {
            Boolean res = taskService.updateStatus(taskVO);
            return ResultModel.success(res, "修改任务状态成功");
        } catch (CommonException e) {
            e.setError(CommonErrorCode.REQUEST_PARAM_ERROR);
            e.setErrMsg("修改操作出现异常");
            return ResultModel.fail(e);
        }
    }


    @DeleteMapping("/plan")
    public ResultModel<Boolean> deletePlan(@RequestParam("id") Integer id) {
        try {
            Boolean res = taskService.deletePlan(id);
            return ResultModel.success(res, "删除成功");
        } catch (CommonException e) {
            e.setError(CommonErrorCode.NOT_EXIST);
            e.setErrMsg("删除操作出现异常");
            return ResultModel.fail(e);
        }
    }
}
