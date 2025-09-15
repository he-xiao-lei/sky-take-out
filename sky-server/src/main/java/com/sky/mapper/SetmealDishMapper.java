package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    //查询菜品id对应套餐id
    List<Long> getSetmealIdsByDishIds(List<Long> ids);
//    @AutoFill(OperationType.INSERT)
    void insertBatch(List<SetmealDish> setmealDishes);
    
    @Select("select * from setmeal_dish where setmeal_id  = #{id}")
    List<SetmealDish> getDishesBySetMealId(Integer id);
}
