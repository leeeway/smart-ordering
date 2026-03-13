package com.example.order.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gson 配置类
 *
 * @author leeway
 * @since 2026/02/01
 */
@Configuration
public class GsonConfig {

    /**
     * 创建 Gson Bean
     *
     * @return Gson 实例
     */
    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
    }
}
