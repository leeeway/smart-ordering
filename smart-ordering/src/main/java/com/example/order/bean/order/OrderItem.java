package com.example.order.bean.order;

import lombok.Data;
import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 订单项实体类
 *
 * @author leeway
 * @since 2026/02/01
 */
@Data
public class OrderItem {

    /**
     * 商品ID
     */
    @NotBlank(message = "商品ID不能为空")
    private String productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 购买数量
     */
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为1")
    private Integer quantity;
}
