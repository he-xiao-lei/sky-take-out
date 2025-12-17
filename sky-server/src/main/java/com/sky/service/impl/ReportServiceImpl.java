package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final DishMapper dishMapper;
    private final WorkspaceService workspaceService;
    
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
            map.put("end", endTime);
            Integer totalUser = userMapper.countByMap(map);
            //再查询出新增用户数量
            map.put("begin", startTime);
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
    
    
    @Override
    public OrderReportVO getOrderReportVOResult(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        
        dateList.add(begin);
        while (!begin.equals(end)) {
            // 计算日期
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> validOrderCountList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime startTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", startTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Integer dayValidOrder = orderMapper.countOrders(map);
            validOrderCountList.add(dayValidOrder);
            map = new HashMap();
            map.put("begin", startTime);
            map.put("end", endTime);
            Integer dayOrderCount = orderMapper.countOrders(map);
            orderCountList.add(dayOrderCount);
        }
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        
        Double completionRate = (validOrderCount.doubleValue() / totalOrderCount);
        
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .validOrderCount(validOrderCount)
                .totalOrderCount(totalOrderCount)
                .orderCountList(StringUtils.join(orderCountList, ","))
                .orderCompletionRate(completionRate)
                .build();
    }
    
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        
        LocalDateTime startTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(startTime, endTime);
        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");
        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");
        
        
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }
    
    @Override
    public void exportBusinessData(HttpServletResponse response) throws IOException {
        //1.查询数据库,获取数据--最近30天
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        // 查询概览数据
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        //2.使用POI将文件写入到excel文件
        try (InputStream resourceAsStream = this.getClass().getResourceAsStream("/template/运营数据报表模板.xlsx")) {
            XSSFWorkbook excel = new XSSFWorkbook(Objects.requireNonNull(resourceAsStream));
            //填充数据,获取表格文件的sheet标签页
            XSSFSheet sheet1 = excel.getSheet("Sheet1");
            //设置日期
            sheet1.getRow(1).getCell(1).setCellValue("时间" + begin + "至" + end);
            //获得第四行
            XSSFRow row4 = sheet1.getRow(3);
            row4.getCell(2).setCellValue(businessData.getTurnover());
            row4.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row4.getCell(6).setCellValue(businessData.getNewUsers());
            XSSFRow row5 = sheet1.getRow(4);
            row5.getCell(2).setCellValue(businessData.getValidOrderCount());
            row5.getCell(4).setCellValue(businessData.getUnitPrice());
            
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                LocalDateTime startTime = LocalDateTime.of(date, LocalTime.MIN);
                LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
                BusinessDataVO businessData1 = workspaceService.getBusinessData(startTime, endTime);
                
                XSSFRow row = sheet1.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData1.getTurnover());
                row.getCell(3).setCellValue(businessData1.getValidOrderCount());
                row.getCell(4).setCellValue(businessData1.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData1.getUnitPrice());
                row.getCell(6).setCellValue(businessData1.getNewUsers());
                
            }
            //3.使用输出流将excel文件下载到客户端浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            outputStream.close();
            excel.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        
        
        
    }
}
