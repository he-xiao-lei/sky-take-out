package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.vo.SetmealVO;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
        
        // 插入套餐数据
        setmealMapper.insert(setmeal);
        // 获取套餐id(主键返回)
        Long id = setmeal.getId();
        // 获取前端传过来的套餐里的菜品数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //判断是否为空
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            // 批量为每个菜品关联套餐id
            setmealDishes.forEach(dish -> dish.setSetmealId(id));
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }
    
    @Override
    public PageResult queryPages(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<Setmeal> result = setmealMapper.queryPages(setmealPageQueryDTO);
        
        return new PageResult(result.getTotal(), result.getResult());
    }
    
    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id)
                .build();
        // 查询出单个套餐所包含的菜品
        List<Dish> dishsBySetMealIds = setmealDishMapper.getDishsBySetMealId(id);
        
        dishsBySetMealIds.forEach(dish -> {
            if (dish.getStatus().equals(StatusConstant.DISABLE)){
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        });
        setmealMapper.update(setmeal);
    }
    
    
    @Override
    public SetmealVO getSetMealById(Long id) {
        // 查出套餐数据
        Setmeal setMeal = setmealMapper.getSetMealById(id);
        Long setMealId = setMeal.getId();
        //查出套餐里菜品数据
        List<SetmealDish> dishBySetmealById = setmealDishMapper.getDishesBySetMealId(id);
        dishBySetmealById.forEach(dish -> dish.setSetmealId(setMealId));
        
        SetmealVO setmeal = new SetmealVO();
        BeanUtils.copyProperties(setMeal, setmeal);
        setmeal.setSetmealDishes(dishBySetmealById);
        return setmeal;
    }
    
    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        //查看套餐是否在起售中
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getSetMealById(id);
            if(setmeal.getStatus().equals(StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
            
            setmealMapper.deleteByIds(ids);
            setmealDishMapper.deleteBySetmealIds(ids);
            
        });
        
        
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 修改套餐基本表
        setmealMapper.update(setmeal);
        
        // 先删除所有菜品
        setmealDishMapper.deleteBySetmealIds(Collections.singletonList(setmeal.getId()));
        // 所有菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        // 重新绑定菜品于套餐
        Long setmealId = setmeal.getId();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.insertBatch(setmealDishes);
    }
    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }
    
    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    } 
}
