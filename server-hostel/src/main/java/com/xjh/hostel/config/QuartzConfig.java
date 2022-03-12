package com.xjh.hostel.config;

import com.xjh.common.utils.PropertyLoader;
import com.xjh.core.quartz.RankingExecJob;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration("QuartzConfig")
public class QuartzConfig {
    @Resource
    private DataSource dataSource;

    public QuartzConfig() {
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(@Qualifier("rankingTriggers") Trigger[] triggers, JobFactory jobFactory, PlatformTransactionManager transactionManager) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setSchedulerName("RankingQuartzScheduler");
        factory.setDataSource(this.dataSource);
        factory.setStartupDelay(10);
        factory.setJobFactory(jobFactory);
        factory.setApplicationContextSchedulerContextKey("applicationContext");
        factory.setTriggers(triggers);
        factory.setTransactionManager(transactionManager);
        factory.setTaskExecutor(new ThreadPoolExecutor(10, 100, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue(200)));
        factory.setConfigLocation(new ClassPathResource(("quartz.properties")));
        factory.setWaitForJobsToCompleteOnShutdown(true);
        factory.setOverwriteExistingJobs(true);
        return factory;
    }

    @Bean(
            name = {"rankingTriggers"}
    )
    public Trigger[] loadJobTriggers() throws ParseException {
        List<Trigger> triggerList = new ArrayList();
        String cron = PropertyLoader.getProperty("ranking.cron");
        triggerList.add(createTrigger(RankingExecJob.class, cron));
        return (Trigger[])triggerList.toArray(new Trigger[triggerList.size()]);
    }

    private static Trigger createTrigger(Class<RankingExecJob> jobClass, String cronExpression) throws ParseException {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(jobClass);
        jobDetailFactoryBean.setName(jobClass.getSimpleName());
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(true);
        jobDetailFactoryBean.afterPropertiesSet();
        JobDetail jobDetail = jobDetailFactoryBean.getObject();
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setName(jobClass.getSimpleName());
        factoryBean.setCronExpression(cronExpression);
        factoryBean.afterPropertiesSet();
        return (Trigger) factoryBean.getObject();
    }
}