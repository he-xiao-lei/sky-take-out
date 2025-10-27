package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {
    // 用户下单方法
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
}
