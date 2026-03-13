package com.example.order.bean.order;

import lombok.Data;
import java.util.List;

@Data
public class RecentOrderDto {
    private String customerName;
    private List<String> products;
    private String timeAgo;
}
