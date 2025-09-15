package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    //查询菜品id对应套餐id
    List<Long> getSetmealIdsByDishIds(List<Long> ids);
//    @AutoFill(OperationType.INSERT)
    void insertBatch(List<SetmealDish> setmealDishes);
    
    @Select("select * from setmeal_dish where id  = #{id}")
    List<SetmealDish> getDishBySetmealById(Integer id);
}
