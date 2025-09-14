package com.sky.service;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;

public interface SetmealService {
    void save(SetmealDTO setmealDTO);
    
    PageResult queryPages(SetmealPageQueryDTO setmealPageQueryDTO);
    
    void startOrStop(Integer status, Long id);
//    SetmealVO getSetMealById(Integer id);
}
