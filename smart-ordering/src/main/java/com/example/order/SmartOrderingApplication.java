package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Description: 启动类
 * @Author: leeway
 * @Date: 2023/8/16/016 15:47
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableAspectJAutoProxy(exposeProxy = true)
public class SmartOrderingApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartOrderingApplication.class, args);
    }
}
