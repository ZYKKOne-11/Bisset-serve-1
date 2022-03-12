package com.xjh.hostel.controller;

import com.xjh.common.enums.RankingTypeEnum;
import com.xjh.common.exception.CommonException;
import com.xjh.common.model.ResultModel;
import com.xjh.common.po.UserPO;
import com.xjh.core.service.hostel.HostelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hostel")
public class HostelController {

    @Resource
    private HostelService hostelService;

    @GetMapping("/ranking/query")
    public ResultModel<Map<RankingTypeEnum,List<UserPO>>> queryRanking(){
        try {
            Map<RankingTypeEnum, List<UserPO>> res = hostelService.queryRanking();
            return ResultModel.success(res);
        }catch (CommonException e){
            return ResultModel.fail(e);
        }
    }
}
