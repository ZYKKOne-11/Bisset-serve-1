package com.xjh.core.quartz;

import com.xjh.common.consts.HostelListConst;
import com.xjh.common.po.UserPO;
import com.xjh.common.utils.HostelRankingUtils;
import com.xjh.core.service.redis.RedisKeyCenter;
import com.xjh.core.service.redis.RedisService;
import com.xjh.core.service.user.UserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingExecJob implements Job {

    private static Logger logger = LoggerFactory.getLogger(RankingExecJob.class);

    @Resource
    private RedisService redisService;

    @Resource
    private UserService userService;

    @Resource
    private HostelListConst hostelListConst;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //TODO 排行榜定时任务
        try{
            logger.info("排行榜定时任务开始执行");
            //更新总排行榜缓存
            String key = RedisKeyCenter.getUserOverAllRankingRedisKey();
            ZSetOperations forZSet = redisService.getRedisTemplate().opsForZSet();
            List<Long> list = iteratorZSet(forZSet, key);
            List<UserPO> res = getUserList(list);
            hostelListConst.setOverAllRankingList(res);
            //更新月排行榜缓存
            key = RedisKeyCenter.getUserMonthRankingRedisKey(String.valueOf(HostelRankingUtils.getNowNumberMonth(System.currentTimeMillis())));
            forZSet = redisService.getRedisTemplate().opsForZSet();
            list = iteratorZSet(forZSet, key);
            res = getUserList(list);
            hostelListConst.setOverAllRankingList(res);
            //更新周排行榜缓存
            key = RedisKeyCenter.getUserMonthRankingRedisKey(String.valueOf(HostelRankingUtils.getNowNumberWeek(System.currentTimeMillis())));
            forZSet = redisService.getRedisTemplate().opsForZSet();
            list = iteratorZSet(forZSet, key);
            res = getUserList(list);
            hostelListConst.setOverAllRankingList(res);
            logger.info("排行榜定时任务执行完毕");
        }catch (Exception e){
            //降级处理
            logger.error("排行榜定时任务执行失败");
        }
    }

    private List<UserPO> getUserList(List<Long> list) {
        //TODO
        Map<Long,UserPO> map = new HashMap<>();
        List<UserPO> res = new ArrayList<>();
        List<UserPO> userPOS = userService.selectUserList(list);
        for (UserPO user:userPOS){
            map.put(user.getUserId(),user);
        }
        for (Long id:list){
            res.add(map.get(id));
        }
        return res;
    }

    private List<Long> iteratorZSet(ZSetOperations opsForZSet,String key){
        List<Long> res = new ArrayList<>();
        Cursor<ZSetOperations.TypedTuple<Object>> scan = opsForZSet.scan(key, ScanOptions.NONE);
        while (scan.hasNext()){
            ZSetOperations.TypedTuple<Object> item = scan.next();
            Long userId = Long.parseLong(String.valueOf(item.getValue()));
            res.add(userId);
        }
        logger.info("遍历key= "+key+" 的zSet,userId 集合为："+res.toString());
        return res;
    }
}