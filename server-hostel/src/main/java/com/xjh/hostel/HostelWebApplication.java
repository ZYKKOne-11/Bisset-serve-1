package com.xjh.hostel;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@MapperScan("com.xjh.core.mapper")
@SpringBootApplication(scanBasePackages = "com.xjh")
public class HostelWebApplication {
    private static Logger logger = LoggerFactory.getLogger(HostelWebApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(HostelWebApplication.class, args);
        logger.info("=============================spring boot start successful !=============================");
    }
}
