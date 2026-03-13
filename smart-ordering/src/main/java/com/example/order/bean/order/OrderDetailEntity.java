package com.example.order.bean.order;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 订单详情表实体类
 */
@Data
public class OrderDetailEntity {
    private Integer id;
    private String orderNo;
    private String productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
}
