package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "C端-用户下单相关类")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    /**
     * 用户下单
     * @param ordersSubmitDTO 前端传输过来的数据
     * @return 下单后的信息
     */
    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("订单信息{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }
    @GetMapping("/historyOrders")
    @ApiOperation("查看历史订单")
    public Result<PageResult> page(int page,int pageSize,Integer status){
            PageResult pageResult =orderService.pageQuery4User(page,pageSize,status);
            return Result.success(pageResult);
    
    }
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详细")
    public Result<OrderVO> details(@PathVariable("id") Integer id){
        OrderVO orderVo=orderService.details(id);
        return Result.success(orderVo);
    }
    
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result<OrderVO> cancel(@PathVariable("id") Integer id){
        orderService.cancelById(id);
        return Result.success();
    }
    /**
     * 再来一单
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable("id") Long id){
        orderService.repetition(id);
        return Result.success();
    }
    
}
