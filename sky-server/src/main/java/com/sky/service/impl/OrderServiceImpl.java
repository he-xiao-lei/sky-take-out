package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final AddressBookMapper addressBookMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        
        // 处理业务异常 1. 收货地址为空 2. 购物车数据为空
        if(addressBookMapper.getById(ordersSubmitDTO.getAddressBookId()) == null)
            // 抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        
        ShoppingCart cart = new ShoppingCart();
        Long userId= BaseContext.getCurrentId();
        cart.setUserId(userId);
        
        if(shoppingCartMapper.list(cart) == null && shoppingCartMapper.list(cart).size() > 0)
            // 抛出业务异常
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        // 向订单表插入一条数据
        Orders orders = new Orders();
        
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        
        orders.setOrderTime(LocalDateTime.now());
        // 未支付
        orders.setPayStatus(Orders.UN_PAID);
        // 代付款
        orders.setStatus(Orders.PENDING_PAYMENT);
        //当前时间的时间戳作为订单号
        Random random = new Random();
        String s = String.valueOf(random.nextInt(1, 1000));
        orders.setNumber(System.currentTimeMillis() + s);
        //手机号
        // 通过地址簿查出手机号
        AddressBook addressBook = addressBookMapper.getById(orders.getAddressBookId());
        String phone = addressBook.getPhone();
        orders.setPhone(phone);
        // 收货人
        String consignee = addressBook.getConsignee();
        orders.setConsignee(consignee);
        
        
        // 设置操作的用户的id
        orders.setUserId(userId);
        
        
        // 执行Mapper文件插入
        orderMapper.insert(orders);
        
        //向订单详细表插入多条数据
        
        //清空购物车
        
        //封装VO返回结果
        return null;
    }
}
