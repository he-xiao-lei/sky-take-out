package com.sky.controller.admin;

import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "服务端订单管理")
public class OrderController {
    private final OrderService orderService;
    
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("搜索订单{}",ordersPageQueryDTO);
        PageResult pageResult =orderService.conditionSearch(ordersPageQueryDTO);
        
        return Result.success(pageResult);
    }
    @GetMapping("/statistics")
    @ApiOperation("查询各个状态订单的数量")
    public Result<OrderStatisticsVO> statistics(){
        OrderStatisticsVO orderStatistics = orderService.getOrderStatistics();
        return Result.success(orderStatistics);
    }
    @GetMapping("/details/{id}")
    @ApiOperation("查看订单详情")
    public Result<OrderVO> details(@PathVariable Integer id){
        OrderVO details = orderService.details(id);
        return Result.success(details);
    }
    /**
     * 接单
     *
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }
}
