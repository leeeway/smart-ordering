package com.example.order.bean.order;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 下单请求 DTO
 *
 * @author leeway
 * @since 2026/02/01
 */
@Data
public class OrderRequest {

    /**
     * 工位地址
     */
    @NotBlank(message = "工位地址不能为空")
    private String workstationAddress;

    /**
     * 下单人姓名（可选）
     */
    private String customerName;

    /**
     * 联系电话（可选）
     */
    private String phoneNumber;

    /**
     * 备注信息（可选）
     */
    private String remark;

    /**
     * 订单项列表
     */
    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItem> items;
}
