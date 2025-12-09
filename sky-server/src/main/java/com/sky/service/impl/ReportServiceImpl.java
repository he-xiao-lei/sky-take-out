package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
//import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final UserMapper userMapper;
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
        for (LocalDate data : dateList) {
            //查询当前日期的营业额(状态为“已完成”订单的金额合计)
            LocalDateTime start = LocalDateTime.of(data, LocalTime.MIN);
            LocalDateTime out = LocalDateTime.of(data, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", start);
            map.put("end", out);
            map.put("status", Orders.COMPLETED);
            
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            
            
            turnoverList.add(turnover);
        }
        
        
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
        
        
    }
    
    @Override
    public UserReportVO getUserReportVOResult(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        
        dateList.add(begin);
        while (!begin.equals(end)) {
            // 计算日期
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //存放每天新增用户数量
        List<Integer> newUserList = new ArrayList<>();
        //存放每天的总用户数量
        List<Integer> totalUserList = new ArrayList<>();
        
        
        for (LocalDate date : dateList) {
            LocalDateTime startTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //先查询出总用户数量
            Map map = new HashMap();
            map.put("end",endTime);
            Integer totalUser = userMapper.countByMap(map);
            //再查询出新增用户数量
            map.put("begin",startTime);
            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }
        
        //封装结果数据
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }
}
