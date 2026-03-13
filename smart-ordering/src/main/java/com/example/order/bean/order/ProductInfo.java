package com.example.order.bean.order;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 商品信息实体类
 *
 * @author leeway
 * @since 2026/02/01
 */
@Data
public class ProductInfo {

    /**
     * 商品ID
     */
    private String id;

    /**
     * 所属店铺ID
     */
    private String shopId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品图片URL
     */
    private String imageUrl;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 是否上架
     */
    private Boolean available;

    /**
     * 商品库存
     */
    private Integer stock;
}
