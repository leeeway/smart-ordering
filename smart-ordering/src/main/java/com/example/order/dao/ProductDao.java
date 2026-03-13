package com.example.order.dao;

import com.example.order.bean.order.ProductInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

/**
 * 商品数据访问接口
 */
@Mapper
public interface ProductDao {

    @Select("SELECT ProductId as id, ShopId, ProductName as name, Price, Description, ImageUrl, Available, Stock FROM ProductInfo WHERE Available = 1 and Stock >0 ")
    List<ProductInfo> getAllAvailableProducts();

    @Select("SELECT ProductId as id, ShopId, ProductName as name, Price, Description, ImageUrl, Available, Stock FROM ProductInfo WHERE ShopId = #{shopId} AND Available = 1 and Stock >0")
    List<ProductInfo> getProductsByShopId(@Param("shopId") String shopId);

    @Select("SELECT ProductId as id, ShopId, ProductName as name, Price, Description, ImageUrl, Available, Stock FROM ProductInfo WHERE ProductId = #{productId}")
    ProductInfo getProductById(@Param("productId") String productId);

    @Update("UPDATE ProductInfo SET Stock = Stock - #{quantity}, UpdateTime = GETDATE() " +
            "WHERE ProductId = #{productId} AND Stock >= #{quantity}")
    int reduceStock(@Param("productId") String productId, @Param("quantity") Integer quantity);
}
