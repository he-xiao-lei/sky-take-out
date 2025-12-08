package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
//import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.sky.entity.Orders.COMPLETED;

@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {
    private final OrderMapper orderMapper;
    @Override
    public TurnoverReportVO getturnoverStatistics(LocalDate begin, LocalDate end) {
        //当前集合用户存储从begin到end的日期
        List<LocalDate> dateList = new ArrayList<>();
        
        dateList.add(begin);
        while (!begin.equals(end)) {
            // 计算日期
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        System.out.println(dateList);
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate data: dateList) {
            //查询当前日期的营业额(状态为“已完成”订单的金额合计)
            LocalDateTime start = LocalDateTime.of(data, LocalTime.MIN);
            LocalDateTime out = LocalDateTime.of(data, LocalTime.MAX);
            Map map  = new HashMap();
            map.put("begin",start);
            map.put("end",out);
            map.put("status", Orders.COMPLETED);
            
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            
            
            
            
            turnoverList.add(turnover);
        }
        
        
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
        
        
    }
}
