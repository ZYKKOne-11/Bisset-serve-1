package com.xjh.hostel.job;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RankingJobFactory extends AdaptableJobFactory {
    /**
     * AutowireCapableBeanFactory接口是BeanFactory的子类，可以连接和填充那些生命周期不被Spring管理的已存在的bean实例
     */
    @Resource
    private AutowireCapableBeanFactory capableBeanFactory;

    /**
     * @Author: liangbl
     * @Date: 2019/1/22 11:06
     * @Description: 任务工厂JobFactory
     * 解决SpringBoot不能在Quartz中注入Bean的问题
     */
    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        // 实例化对象
        Object jobInstance = super.createJobInstance(bundle);
        // 进行注入（Spring管理该Bean）
        capableBeanFactory.autowireBean(jobInstance);
        //返回对象
        return jobInstance;
    }
}

