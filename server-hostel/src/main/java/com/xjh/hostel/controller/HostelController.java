package com.xjh.hostel.controller;

import com.xjh.common.bean.Share;
import com.xjh.common.enums.RankingTypeEnum;
import com.xjh.common.exception.CommonException;
import com.xjh.common.model.ResultModel;
import com.xjh.common.po.UserPO;
import com.xjh.common.vo.HostelVO;
import com.xjh.core.service.hostel.HostelService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/hostel")
public class HostelController {

    @Resource
    private HostelService hostelService;

    /**
     * 用户排行榜信息查询
     * @return
     */
    @GetMapping("/rank/query")
    public ResultModel<Map<RankingTypeEnum,List<UserPO>>> queryRanking(){
        try {
            Map<RankingTypeEnum, List<UserPO>> res = hostelService.queryRanking();
            return ResultModel.success(res);
        }catch (CommonException e){
            return ResultModel.fail(e);
        }
    }

    @PostMapping("/share")
    public ResultModel<Boolean> userShare(@RequestBody HostelVO hostelVO, HttpServletRequest request){
        try{
            Boolean res = hostelService.userShare(hostelVO,request);
            return ResultModel.success(res);
        }catch (CommonException e){
            return ResultModel.fail(e);
        }
    }
}
