package com.example.order.bean.order;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单主表实体类
 */
@Data
public class OrderMainEntity {
    private Integer id;
    private String orderNo;
    private String customerName;
    private String workstationAddress;
    private String phoneNumber;
    private String remark;
    private BigDecimal totalPrice;
    private Date createTime;
}
