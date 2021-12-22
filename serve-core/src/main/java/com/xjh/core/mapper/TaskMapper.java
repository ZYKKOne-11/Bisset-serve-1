package com.xjh.core.mapper;

import com.xjh.common.bean.Task;
import com.xjh.common.po.PlanTaskPO;
import com.xjh.common.vo.RequestTaskVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


public interface TaskMapper {


    void deleteTask(Integer id);

    int insertPlan(PlanTaskPO planTaskPO);

    void insertTask(List<Task> t);

    void updatePlan(PlanTaskPO planTaskPO);

    void deletePlanAndTask(Integer id);

    void updateStatus(RequestTaskVO taskVO);

    List<PlanTaskPO> queryAlivePlan(@Param("taskVO") RequestTaskVO taskVO, @Param("date") Date date);

    List<PlanTaskPO> queryHistoryPlan(@Param("taskVO") RequestTaskVO taskVO, @Param("date") Date date);

//    List<PlanTaskPO> getDataByTaskVo(RequestTaskVO taskVO);
//
//    Integer getCountByTaskVo(RequestTaskVO taskVO);
}
