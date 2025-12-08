//package com.sky.mapper;
//
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Select;
//
//import javax.validation.constraints.Max;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Mapper
//public interface ReportMapper {
//    @Select("select sum(amount) from orders where status = 5 and order_time > #{start} and order_time < #{out} ")
//    Double getAmountByDate(LocalDateTime start,LocalDateTime out);
//}
