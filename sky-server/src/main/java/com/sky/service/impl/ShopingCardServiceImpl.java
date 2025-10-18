package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShopingCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopingCardServiceImpl implements ShopingCardService {
    private final ShoppingCartMapper shoppingCartMapper;
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
        if(!list.isEmpty() && list.size() >0){
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.update(cart);
        }else {
        
        }
        // 如果不存在，需要创建一个新的购物车
        
    }
}
