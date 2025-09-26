package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {
    private final DishMapper dishMapper;
    private final DishFlavorMapper dishFlavorMapper;
    private final SetmealDishMapper setmealDishMapper;
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
    
    @Override
    public void deleteByIds(List<Long> ids) {
        ids.forEach(id->{
            Dish dish = dishMapper.getById(id);
            // 判断菜品是否存在起售中
            if(dish.getStatus().equals(StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });
        
        // 判断当前菜品是否被套餐关联
        List<Long> setmealIdsByDishIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIdsByDishIds != null && !setmealIdsByDishIds.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
//        ids.forEach(id->{
//            //则可以删除菜品
//            dishMapper.deleteById(id);
//            // 删除关联的口味数据
//            dishFlavorMapper.deleteByDishId(id);
//        });
     // 批量删除菜品集合
        dishMapper.deleteByIds(ids);
        // 删除口味集合
        dishFlavorMapper.deleteByDishIds(ids);
     
    }
    /**
     * 根据id查询菜品和口味的数据
     * @param id 员工id
     * @return 给前端的渲染对象
     */
    @Override
    public DishVO getDishWithFlavorById(Long id) {
        //根据id查询菜品数据
        Dish byId = dishMapper.getById(id);
        //根据id查询出口味数据
        List<DishFlavor> dishFlavorById = dishFlavorMapper.getDishFlavorById(id);
        
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(byId,dishVO);
        dishVO.setFlavors(dishFlavorById);
        return dishVO;
    }
    
    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //修改菜品基本表
        dishMapper.update(dish);
        // 删除原有口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()){
            // 口味表多条数据
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
        
        
    }
    
    @Override
    public List<DishVO> getDishByCategoryId(Integer categoryId) {
        
        return dishMapper.getByCategoryId(categoryId);
    }
    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);
        
        List<DishVO> dishVOList = new ArrayList<>();
        
        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);
            
            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getDishFlavorById(d.getId());
            
            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }
        
        return dishVOList;
    }
    
}
