package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    void insertBatch(List<DishFlavor> flavors);
    @Delete("delete from dish_flavor where id = #{dishId}")
    void deleteByDishId(Long dishId);
    //根据菜品id集合删除口味数据
    //delete from dish_flavor where id in (?,?,?)
    void deleteByDishIds(List<Long> ids);
    /**
     * 根据id查询菜品和口味的数据
     * @param dishId 员工id
     * @return 给前端的渲染对象
     */
    List<DishFlavor> getDishFlavorById(Long dishId);
    
    
    
}
