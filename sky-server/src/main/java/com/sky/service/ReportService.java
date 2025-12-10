package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    TurnoverReportVO getturnoverStatistics(LocalDate begin, LocalDate end);
    
    /**
     * 统计之间区间内的用户数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 用户报告VO
     */
    UserReportVO getUserReportVOResult(LocalDate begin, LocalDate end);
    
    /**
     * 统计指定区间内订单数据
     * @param begin 开始时间
     * @param end  结束时间
     * @return 订单VO
     */
    OrderReportVO getOrderReportVOResult(LocalDate begin, LocalDate end);
}
