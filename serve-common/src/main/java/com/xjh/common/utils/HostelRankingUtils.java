package com.xjh.common.utils;

import com.xjh.common.consts.HostelListConst;
import com.xjh.common.exception.CommonErrorCode;
import com.xjh.common.exception.CommonException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//客栈用户排行榜时间工具
public class HostelRankingUtils {

    private static final Long WEEK_TIME_MILLIS = 86400000L * 7;

    private static final Long MONTH_TIME_MILLIS = 2592000000L;

    private static final String MONTH_RANKING_START_TIME = "2022-03-07 00:00:00";

    private static final String WEEK_RANKING_START_TIME="2022-03-01 00:00:00";


    public static Integer getNowNumberMonth(Long nowTimeMillis) throws Exception {
        Long oldTimeMillis = getTimeMillis(MONTH_RANKING_START_TIME);
        Long needTimeMillis = nowTimeMillis - oldTimeMillis;
        if (needTimeMillis < 0){
            throw new CommonException(CommonErrorCode.VALIDATE_ERROR,"请求时间异常，请稍后重试");
        }
        return (int)(needTimeMillis / MONTH_TIME_MILLIS) +1;
    }

    public static Integer getNowNumberWeek(Long nowTimeMillis) throws Exception {
        Long oldTimeMillis = getTimeMillis(WEEK_RANKING_START_TIME);
        Long needTimeMillis = nowTimeMillis - oldTimeMillis;
        if (needTimeMillis < 0){
            throw new CommonException(CommonErrorCode.VALIDATE_ERROR,"请求时间异常，请稍后重试");
        }
        return (int)(needTimeMillis / WEEK_TIME_MILLIS) +1;
    }

    private static Long getTimeMillis(String time) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = df.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }
}