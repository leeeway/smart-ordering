package com.example.order.dao;

import com.example.order.bean.order.ShopInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 店铺数据访问接口
 */
@Mapper
public interface ShopDao {

    @Select("SELECT ShopId as id, ShopName as name, Description, AvatarUrl, BackgroundUrl FROM ShopInfo")
    List<ShopInfo> getAllShops();
}
