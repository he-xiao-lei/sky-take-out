package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final AddressBookMapper addressBookMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        
        // 处理业务异常 1. 收货地址为空 2. 购物车数据为空
        if(addressBookMapper.getById(ordersSubmitDTO.getAddressBookId()) == null)
            // 抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId= BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        // 查询出购物车的条数
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list == null && list.size() > 0)
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
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
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
        // 批量插入
        List<OrderDetail> orderDetailList = new ArrayList<>();
        //向订单详细表插入多条数据
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail(); // 订单明细
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());// 设置当前订单详细关联的订单id
            orderDetailList.add(orderDetail);
        }
        // 插入订单详细表
        orderDetailMapper.insertBatch(orderDetailList);

        
        //清空购物车
        shoppingCartMapper.deleteByUserId(userId);
        //封装VO返回结果
        
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderTime(orders.getOrderTime())
                .orderAmount(orders.getAmount())
                .build();
    }
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);
        
        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );
        
        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }
        
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        
        return vo;
    }
    
    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {
        
        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);
        
        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        
        orderMapper.update(orders);
    }
}
