package com.xjh.core.service.Task;

import com.xjh.common.bean.Hotspot;
import com.xjh.common.po.PlanTaskPO;
import com.xjh.common.utils.Page;
import com.xjh.common.vo.RequestTaskVO;

import java.util.List;

public interface TaskService {
    List<PlanTaskPO> getTaskByPlanId(RequestTaskVO taskVO);

    Boolean deletePlan(Integer id);

    Boolean postPlan(PlanTaskPO planTaskPO);

    Boolean updatePlanAndTask(PlanTaskPO planTaskPO);

    Boolean updateStatus(RequestTaskVO taskVO);

    List<Hotspot> queryHotspot();
}
