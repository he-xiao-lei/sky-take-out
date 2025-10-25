package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
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
    @Insert("insert into shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time) VALUE " +
            "(#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);
    @Select("select * from shopping_cart")
    List<ShoppingCart> listAll();
}
