package com.example.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置类
 *
 * @author leeway
 * @since 2026/02/01
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 创建 RestTemplate Bean
     * 配置连接超时和读取超时
     *
     * @return RestTemplate 实例
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 连接超时时间 5秒
        factory.setConnectTimeout(5000);
        // 读取超时时间 10秒
        factory.setReadTimeout(10000);
        return new RestTemplate(factory);
    }
}
