package com.xjh.hostel.controller;

import com.xjh.common.bean.Share;
import com.xjh.common.enums.RankingTypeEnum;
import com.xjh.common.exception.CommonException;
import com.xjh.common.model.ResultModel;
import com.xjh.common.po.UserDiscussPO;
import com.xjh.common.po.UserPO;
import com.xjh.common.po.UserSharePO;
import com.xjh.common.utils.Page;
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

    /**
     * 用户分享学习思维导图
     * @param hostelVO
     * @param request
     * @return
     */
    @PostMapping("/share")
    public ResultModel<Boolean> userPostShare(@RequestBody HostelVO hostelVO, HttpServletRequest request){
        try{
            Boolean res = hostelService.userShare(hostelVO,request);
            return ResultModel.success(res);
        }catch (CommonException e){
            return ResultModel.fail(e);
        }
    }

    /**
     * 获取按时间分页的分享内容
     * @param hostelVO
     * @return
     */
    @PostMapping("/share/query")
    public ResultModel<Page<UserSharePO>> userGetShare(@RequestBody HostelVO hostelVO){
        try {
            Page<UserSharePO> res = hostelService.selectUserShareInfo(hostelVO);
            return ResultModel.success(res);
        }catch (CommonException e){
            return ResultModel.fail(e);
        }
    }

    /**
     * 获取某一分享下可公开的评论
     * @param hostelVO
     * @return
     */
    @PostMapping("/discuss/query")
    public ResultModel<Page<UserDiscussPO>> userGetDiscuss(@RequestBody HostelVO hostelVO){
        try {
            Page<UserDiscussPO> res = hostelService.selectUserDiscussInfo(hostelVO);
            return ResultModel.success(res);
        }catch (CommonException e){
            return ResultModel.fail(e);
        }
    }

    /**
     * 用户就某一分享发表自己的评论
     * @param hostelVO
     * @return
     */
    @PostMapping("/discuss")
    public ResultModel<Boolean> userPostDiscuss(@RequestBody HostelVO hostelVO){
        try {
            Boolean res = hostelService.publishDiscussByShare(hostelVO);
            return ResultModel.success(res);
        }catch (CommonException e){
            return ResultModel.fail(e);
        }
    }
}
