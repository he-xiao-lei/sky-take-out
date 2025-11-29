package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    // 用户下单方法
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;
    
    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);
    
    PageResult pageQuery4User(int page, int pageSize, Integer status);
    
    OrderVO details(Integer id);
    
    void cancelById(Integer id);
    
    void repetition(Long id);
    
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);
    
    OrderStatisticsVO getOrderStatistics();
    
    void confirm(OrdersConfirmDTO ordersConfirmDTO);
    void updatePendingOrderToPaid(String orderNumber);
    
    void rejection(OrdersRejectionDTO ordersRejectionDTO);
    
    void cancel(OrdersCancelDTO ordersCancelDTO);
    
    void delivery(Long id);
}
