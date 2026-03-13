package com.example.order.bean.order;

import lombok.Data;
import java.util.List;

/**
 * 店铺信息实体类
 *
 * @author leeway
 * @since 2026/02/01
 */
@Data
public class ShopInfo {
    /**
     * 店铺ID
     */
    private String id;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺描述
     */
    private String description;

    /**
     * 店铺头像 (唐装小姐姐)
     */
    private String avatarUrl;

    /**
     * 店铺背景图
     */
    private String backgroundUrl;

    /**
     * 商品列表
     */
    private List<ProductInfo> products;
}
