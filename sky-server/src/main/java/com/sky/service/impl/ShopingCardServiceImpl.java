package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShopingCardService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopingCardServiceImpl implements ShopingCardService {
    private final ShoppingCartMapper shoppingCartMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //获取当前userId
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setId(userId);
        // 判断当前加入到购物车里的商品是否已经存在
        
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        //如果存在，需要将数量加1
        if(list!=null && list.size() > 0){
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.update(cart);
        }else {
            // 如果不存在，需要创建一个新的购物车
            // 先查看是菜品还是套餐
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                // 添加的是菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                
                extracted(shoppingCart);
            }else {
                // 添加的是套餐
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getSetMealById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
                
                extracted(shoppingCart);
            }
            // 插入数据
            shoppingCartMapper.insert(shoppingCart);
        }
        
    }
    
    private static void extracted(ShoppingCart shoppingCart) {
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
    }
}
