package com.example.order.dao;

import com.example.order.bean.order.OrderDetailEntity;
import com.example.order.bean.order.OrderMainEntity;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

/**
 * 订单数据访问接口
 */
@Mapper
public interface OrderDao {

        @Insert("INSERT INTO OrderMain (OrderNo, CustomerName, WorkstationAddress, PhoneNumber, Remark, TotalPrice, CreateTime) "
                        +
                        "VALUES (#{orderNo}, #{customerName}, #{workstationAddress}, #{phoneNumber}, #{remark}, #{totalPrice}, GETDATE())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insertOrderMain(OrderMainEntity orderMain);

        @Insert("INSERT INTO OrderDetail (OrderNo, ProductId, ProductName, Price, Quantity) " +
                        "VALUES (#{orderNo}, #{productId}, #{productName}, #{price}, #{quantity})")
        int insertOrderDetail(OrderDetailEntity orderDetail);

        @org.apache.ibatis.annotations.Select("SELECT TOP 20 * FROM OrderMain ORDER BY CreateTime DESC")
        List<OrderMainEntity> selectRecentOrders();

        @org.apache.ibatis.annotations.Select("SELECT * FROM OrderDetail WHERE OrderNo = #{orderNo}")
        List<OrderDetailEntity> selectOrderDetailsByOrderNo(String orderNo);
}
