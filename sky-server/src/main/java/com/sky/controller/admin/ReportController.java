package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.common.NameUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Slf4j
@Api(tags = "数据统计相关接口")
@RequestMapping("/admin/report")
@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) { //日期类型有固定模式，通过注解描述日期格式,
        
        TurnoverReportVO turnoverReportVO = reportService.getturnoverStatistics(begin, end);
        
        return Result.success(turnoverReportVO);
    }
    
    @GetMapping("/userStatistics")
    @ApiOperation("用户数据统计")
    public Result<UserReportVO> getUserReportVOResult(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户数据统计{},{}", begin, end);
        UserReportVO userReportVOResult = reportService.getUserReportVOResult(begin, end);
        
        return Result.success(userReportVOResult);
    }
    
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单数据统计")
    public Result<OrderReportVO> getOrderReportVOResult(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单数据统计{},{}", begin, end);
        OrderReportVO orderReportVOResult = reportService.getOrderReportVOResult(begin, end);
        return Result.success(orderReportVOResult);
    }
    
    @GetMapping("/top10")
    @ApiOperation("获取销量前十")
    public Result<SalesTop10ReportVO> getSalesTop10ReportVO(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("销量前十统计{}{}", begin, end);
        SalesTop10ReportVO salesTop10ReportVO = reportService.getSalesTop10(begin, end);
        
        
        return Result.success(salesTop10ReportVO);
        
    }
    
}
