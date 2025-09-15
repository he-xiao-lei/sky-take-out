package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
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
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(dish -> dish.setSetmealId(id));
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }
    
    @Override
    public PageResult queryPages(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<Setmeal> result = setmealMapper.queryPages(setmealPageQueryDTO);
        
        return new PageResult(result.getTotal(),result.getResult());
    }
    
    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id)
                .build();
        
        setmealMapper.update(setmeal);
    }


    @Override
    public SetmealVO getSetMealById(Integer id) {
        // 查出套餐数据
        Setmeal setMeal = setmealMapper.getSetMealById(id);
        Long setMealId = setMeal.getId();
        //查出套餐里菜品数据
        List<SetmealDish> dishBySetmealById = setmealDishMapper.getDishBySetmealById(id);
        dishBySetmealById.forEach(dish->dish.setSetmealId(setMealId));
        
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setMeal,setmealVO);
        setmealVO.setSetmealDishes(dishBySetmealById);
        return setmealVO;
    }
    
    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
    
    }
}
