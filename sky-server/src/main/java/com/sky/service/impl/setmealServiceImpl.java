package com.sky.service.impl;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class setmealServiceImpl implements SetmealService {
    
    private final SetmealMapper setmealMapper;
    private final SetmealDishMapper setmealDishMapper;
    
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        
        
        setmealMapper.insert(setmeal);
        Long id = setmeal.getId();
        
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.isEmpty()) {
            setmealDishes.forEach(dish -> dish.setSetmealId(id));
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }


//    @Override
//    public SetmealVO getSetMealById(Integer id) {
//        setmealMapper.getSetMealById(id);
//    }
}
