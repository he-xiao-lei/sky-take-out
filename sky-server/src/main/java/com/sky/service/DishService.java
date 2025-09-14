package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    //新增菜品和口味数据
    void saveWithFlavor(DishDTO dishDTO);
    
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
    
    /**
     * 菜品批量删除
     * @param ids
     */
    void deleteById(List<Long> ids);
    
    DishVO getDishWithFlavorById(Long id);
    
    void updateWithFlavor(DishDTO dishDTO);
    
    List<DishVO> getDishByCategoryId(Integer categoryId);
}
