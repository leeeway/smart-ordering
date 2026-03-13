package com.example.order.config;

import com.example.order.bean.order.ShopInfo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品配置类
 * 从配置文件读取商品信息
 *
 * @author leeway
 * @since 2026/02/01
 */
@Data
@Component
@ConfigurationProperties(prefix = "order")
public class ProductConfig {

    /**
     * 店铺列表
     */
    private List<ShopInfo> shops = new ArrayList<>();

    /**
     * 企业微信 WebHook URL
     */
    private String webhookUrl;

    /**
     * 订单功能是否启用
     */
    private Boolean enabled = true;
}
