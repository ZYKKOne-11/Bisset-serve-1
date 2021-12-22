package com.xjh.task;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.xjh.core.mapper")
@SpringBootApplication(scanBasePackages = "com.xjh")
public class TaskWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskWebApplication.class, args);
    }
}
