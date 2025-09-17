package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    public DishService dishService;
    
    @PostMapping
    @ApiOperation(value = "新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }
    
    @GetMapping("/page")
    @ApiOperation(value = "菜品分类查询")
    public Result<PageResult> queryPages(DishPageQueryDTO dishPageQueryDTO){//作为普通参数
        log.info("菜品分类查询{}",dishPageQueryDTO);
        
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        
        return Result.success(pageResult);
        
    }

    @DeleteMapping
    @ApiOperation(value = "批量删除菜品操作")
    @Transactional
    public Result deleteById(@RequestParam List<Long> ids){
        log.info("菜品批量删除{}",ids);
        dishService.deleteByIds(ids);
        System.out.println();
        return Result.success();
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询菜品")
    public Result<DishVO> getDishById(@PathVariable Long id){
        log.info("根据id:{}查询菜品",id);
        DishVO dishWithFlavorById = dishService.getDishWithFlavorById(id);
        return Result.success(dishWithFlavorById);
    }
    
    @PutMapping
    @ApiOperation(value = "菜品修改")
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.updateWithFlavor(dishDTO);
        
        return Result.success();
    }
    
    @GetMapping("/list")
    public Result<List<DishVO>> list(@RequestParam Integer categoryId){
        log.info("查询分类id为{}里的菜",categoryId);
        List<DishVO> dishByCategoryId = dishService.getDishByCategoryId(categoryId);
        
        
        return Result.success(dishByCategoryId);
    }
}
