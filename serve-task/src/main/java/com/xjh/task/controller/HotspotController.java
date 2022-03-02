package com.xjh.task.controller;

import com.xjh.common.bean.Hotspot;
import com.xjh.common.exception.CommonErrorCode;
import com.xjh.common.exception.CommonException;
import com.xjh.common.model.ResultModel;
import com.xjh.core.service.Task.TaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/hotspot")
public class HotspotController {

    @Resource
    private TaskService taskService;

    @GetMapping("/query")
    public ResultModel<List<Hotspot>> Query() {
        try {
            List<Hotspot> hotspot = taskService.queryHotspot();
            return ResultModel.success(hotspot);
        } catch (CommonException e) {
            return ResultModel.fail(e);
        }
    }
}
