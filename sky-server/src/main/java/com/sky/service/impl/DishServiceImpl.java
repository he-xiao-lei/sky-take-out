package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    public DishMapper dishMapper;
    
    @Autowired
    public DishFlavorMapper dishFlavorMapper;
    @Override
    @Transactional//原子性
    public void saveWithFlavor(DishDTO dishDTO) {
        // 菜品表插入1条数据
        Dish dish = new Dish();
        
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        //获取insert语句的主键值
        Long id = dish.getId();//菜品id
        
        
        List<DishFlavor> flavors = dishDTO.getFlavors();
        
        if (flavors != null && !flavors.isEmpty()){
            // 口味表多条数据
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(id));
            dishFlavorMapper.insertBatch(flavors);
        }
        
    }
    
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        
        Page<DishVO> dishVO = dishMapper.queryPages(dishPageQueryDTO);
        
        return new PageResult(dishVO.getTotal(),dishVO.getResult());
        
        
    }
}
