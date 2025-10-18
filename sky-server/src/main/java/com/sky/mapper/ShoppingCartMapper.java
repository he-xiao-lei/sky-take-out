package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    
    List<ShoppingCart> list(ShoppingCart shoppingCart);
    // 根据id修改购物车中商品数量
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void update(ShoppingCart cart);
}
