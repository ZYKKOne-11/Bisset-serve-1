package com.xjh.core.service.redis;


import com.xjh.common.utils.MyDateUtils;

public class RedisKeyCenter {
    public RedisKeyCenter() {
    }
    public static String getUserLoginInfoRedisKey(String token) {
        return String.format("user_token:user_login_info:%s", token);
    }

    public static String getUserEmailInfoRedisKey(String token) {
        return String.format("user_token:user_email_info:%s", token);
    }

    public static String getUserEmailTimeInfoRediskey(String token) {
        return String.format("user_token:email_time_info:%s", token);
    }

    public static String getUserLoginNumberRediskey(String token) {
        return String.format("user_token:user_login_number:%s", token);
    }

    public static String getDoorOpeUserIdRedisKey(String opeToken) {
        return String.format("Door:opeUserId:%s", opeToken);
    }

    public static String getUserOverAllRankingRedisKey(){
        return String.format("Ranking:overAll");
    }

    public static String getUserMonthRankingRedisKey(String month){
        return String.format("Ranking:%s_month",month);
    }

    public static String getUserWeekRankingRedisKey(String week){
        return String.format("Ranking:%s_week",week);
    }
}

