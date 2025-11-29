package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final AddressBookMapper addressBookMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WeChatPayUtil weChatPayUtil;
    private final UserMapper userMapper;
    
    /**
     * 模拟微信支付返回数据
     *
     * @param type 返回类型：success-成功，paid-已支付，error-错误
     */
    public static JSONObject mockWeChatPayResponse(String type) {
        JSONObject jsonObject = new JSONObject();
        
        switch (type) {
            case "success":
                // 支付成功情况
                jsonObject.put("nonceStr", "5K8264ILTKCH16CQ2502SI8ZNMTM67VS");
                jsonObject.put("paySign", "C380BEC2BFD727A4B6845133519F3AD6");
                jsonObject.put("timeStamp", "1414561699");
                jsonObject.put("signType", "RSA");
                jsonObject.put("package", "prepay_id=wx201410272009395522657A690389285100");
                jsonObject.put("prepay_id", "wx201410272009395522657A690389285100");
                break;
            
            case "paid":
                // 订单已支付情况
                jsonObject.put("code", "ORDERPAID");
                jsonObject.put("message", "该订单已支付");
                break;
            
            case "error":
                // 支付错误情况
                jsonObject.put("code", "FAIL");
                jsonObject.put("message", "支付失败");
                break;
            
            default:
                jsonObject.put("code", "UNKNOWN");
                jsonObject.put("message", "未知错误");
        }
        
        return jsonObject;
    }
    
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        
        // 处理业务异常 1. 收货地址为空 2. 购物车数据为空
        if (addressBookMapper.getById(ordersSubmitDTO.getAddressBookId()) == null)
            // 抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        // 查询出购物车的条数
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list == null && list.size() > 0)
            // 抛出业务异常
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        // 向订单表插入一条数据
        Orders orders = new Orders();
        
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        
        orders.setOrderTime(LocalDateTime.now());
        // 未支付
//        orders.setPayStatus(Orders.UN_PAID);
        orders.setPayStatus(Orders.PAID);
        // 代付款
//        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        //当前时间的时间戳作为订单号
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        //手机号
        // 通过地址簿查出手机号
        AddressBook addressBook = addressBookMapper.getById(orders.getAddressBookId());
        orders.setAddress(addressBook.getDetail());
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
            BeanUtils.copyProperties(cart, orderDetail);
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
        
        // 在开发环境中模拟支付成功
        // 如果需要真实支付，可以取消下面的注释并注释掉模拟代码
        /*
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );
        */
        
        // 模拟支付成功
        JSONObject jsonObject = mockWeChatPayResponse("success");
        
        // 检查是否订单已支付
        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }
        
        // 转换为VO对象
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        // 注意：需要手动设置packageStr字段，因为JSON中的字段名是package，而VO中的字段名是packageStr
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
    
    @Override
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        // 设置分页
        PageHelper.startPage(pageNum, pageSize);
        
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);
        
        // 分页条件查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        //返回结果
        List<OrderVO> list = new ArrayList<>();
        
        // 查询出订单明细,并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();//订单ID
                
                // 查询订单明细
                
                List<OrderDetail> orderdetails = orderDetailMapper.getByOrderId(orderId);
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderdetails);
                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }
    
    @Override
    public OrderVO details(Integer id) {
        // 根据id查询订单
        Orders orders = orderMapper.getById(id);
        // 查询该订单对应菜品/套餐明细
        List<OrderDetail> orderdetails = orderDetailMapper.getByOrderId(orders.getId());
        
        
        // 将该订单及其详情封装到OrderVO并且返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderdetails);
        
        
        return orderVO;
    }
    
    @Override
    public void cancelById(Integer id) {
        // 根据id查询订单
        Orders orderDb = orderMapper.getById(id);
        
        // 检验订单是否存在
        if (orderDb == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //订单状态1 代付款 2待接单 3已接单 4派送中 5已完成 6已
        if (orderDb.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = new Orders();
        orders.setId(orderDb.getId());
        
        //订单处于待接单状态取消，需要进行退款
        if (orderDb.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            log.warn("已退款");
            orders.setPayStatus(Orders.REFUND);
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
        
    }
    
    @Override
    public void repetition(Long id) {
        // 查询当前用户id
        Long userId = BaseContext.getCurrentId();
        // 根据订单id查询当前订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        
        // 将订单对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(
                orderDetail -> {
                    ShoppingCart shoppingCart = new ShoppingCart();
                    // 将原订单详情里面的菜品信息重新复制到购物车对象中
                    BeanUtils.copyProperties(orderDetail, shoppingCart, "id");
                    shoppingCart.setUserId(userId);
                    shoppingCart.setCreateTime(LocalDateTime.now());
                    return shoppingCart;
                }
        ).collect(Collectors.toList());
        // 将购物车对象批量添加到数据库中
        shoppingCartMapper.insertBatch(shoppingCartList);
    }
    
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        
        // 部分订单状态,需要额外返回订单信息,将Orders转化为OrderVO
        List<OrderVO> orderVOList = getOrderList(page);
        return new PageResult(page.getTotal(), orderVOList);
    }
    
    @Override
    public OrderStatisticsVO getOrderStatistics() {
        //根据状态查询出各个状态订单的数量
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);
        
        // 将查询到的数量封状态OrderStatisticsVO
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        
        return orderStatisticsVO;
    }
    
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        
        Orders build = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(build);
        
    }
    
    
    public List<OrderVO> getOrderList(Page<Orders> page) {
        // 需要返回订单菜品信息，自定义OrderVO响应结果
        List<OrderVO> orderVOList = new ArrayList<>();
        
        List<Orders> orders = page.getResult();
        if (!CollectionUtils.isEmpty(orders)) {
            for (Orders order : orders) {
                //共同字段复制到OrderVO
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(order, orderVO);
                String orderDetails = getOrderDishesStr(order);
                
                orderVO.setOrderDishes(orderDetails);
                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }
    
    private String getOrderDishesStr(Orders orders) {
        //查询订单菜品详细信息(订单中的菜品和数量)
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());
        //将每一个订单菜品信息拼接为字符串(格式；宫保鸡丁*3)
        List<String> collect = orderDetailList.stream().map(x ->
                x.getName() + "*" + x.getNumber() + ";"
        ).collect(Collectors.toList());
        //将该订单对应的所有菜品信息拼接到一起
        return String.join("", collect);
    }
    
    /**
     * 将订单从待付款状态修改为已付款状态
     * 同时更新订单状态为待接单，支付状态为已支付
     *
     * @param orderNumber 订单号
     */
    public void updatePendingOrderToPaid(String orderNumber) {
        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(orderNumber);
        
        // 检查订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException("订单不存在");
        }
        
        // 检查订单是否为待付款状态
        if (!Orders.PENDING_PAYMENT.equals(ordersDB.getStatus())) {
            throw new OrderBusinessException("订单不是待付款状态");
        }
        
        // 检查支付状态是否为未支付
        if (!Orders.UN_PAID.equals(ordersDB.getPayStatus())) {
            throw new OrderBusinessException("订单已支付");
        }
        
        // 更新订单状态为待接单，支付状态为已支付
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)  // 订单状态改为待接单
                .payStatus(Orders.PAID)          // 支付状态改为已支付
                .checkoutTime(LocalDateTime.now())
                .build();
        
        orderMapper.update(orders);
    }
    
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        //根据id查询订单
        Orders ordersDB = orderMapper.getById(Math.toIntExact(ordersRejectionDTO.getId()));
        
        //订单只有存在且状态为2(待接单)才可以拒单
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //支付状态
        Integer payStatus = ordersDB.getPayStatus();
        if (payStatus.equals(Orders.PAID)) {
            //用户已支付需要退款
            log.info("退款完成");
        }
        //拒单需要退款，根据订单id更新订单状态，拒单原因，取消时间
        Orders orders = new Orders();
        orders.setId(ordersRejectionDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }
    
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        //根据id查询订单
        Orders ordersDB = orderMapper.getById(Math.toIntExact(ordersCancelDTO.getId()));
        //支付状态
        Integer payStatus = ordersDB.getPayStatus();
        if (payStatus.equals(Orders.PAID)) {
            //用户已支付需要退款
            log.info("退款完成");
        }
        //拒单需要退款，根据订单id更新订单状态，拒单原因，取消时间
        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }
    
    @Override
    public void delivery(Long id) {
        Orders ordersDB = orderMapper.getById(Math.toIntExact(id));
        
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        
        
        Orders build = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();
        orderMapper.update(build);
    }
    
    @Override
    public void complete(Integer id) {
        Orders ordersDB = orderMapper.getById(id);
        
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        
        Orders build = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.COMPLETED)
                .build();
        orderMapper.update(build);
    }
}

