package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    TurnoverReportVO getturnoverStatistics(LocalDate begin, LocalDate end);
}
