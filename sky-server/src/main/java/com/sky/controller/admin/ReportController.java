package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    ){ //日期类型有固定模式，通过注解描述日期格式,
        
        TurnoverReportVO turnoverReportVO = reportService.getturnoverStatistics(begin, end);
        
        return Result.success(turnoverReportVO);
    }
    
    
}
