package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    //查询菜品id对应套餐id
    List<Long> getSetmealIdsByDishIds(List<Long> ids);
    
    void insertBatch(List<SetmealDish> setmealDishes);
}
