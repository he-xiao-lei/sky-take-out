package com.sky.controller.admin;
import com.sky.vo.SetmealVO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@RequiredArgsConstructor
@Slf4j
public class setmealController {
    private final SetmealService setMealService;
    
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询套餐")
    public Result<SetmealVO> getSetMealById(@PathVariable Long id) {
        SetmealVO setmealvo = setMealService.getSetMealById(id);
        log.info("套餐id为{}的信息{}", id, setmealvo);
        return Result.success(setmealvo);
    }
    
    @PostMapping("/status/{status}")
    @ApiOperation(value = "设置套餐状态")
    @CacheEvict(value = "setmealCache",allEntries = true)
    public Result startOrStop(@PathVariable(value = "status")Integer status,Long id){
        setMealService.startOrStop(status,id);
        return Result.success();
    }
    @GetMapping("/page")
    @ApiOperation(value = "分页查询套餐")
    public Result<PageResult> queryPages(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("查询套餐参数{}",setmealPageQueryDTO);
        PageResult pageResult = setMealService.queryPages(setmealPageQueryDTO);
        
        return Result.success(pageResult);
    }
    
    @PostMapping
    @ApiOperation(value = "新增套餐方法")
    @Cacheable(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        setMealService.save(setmealDTO);
        return Result.success();
    }
    
    
    @DeleteMapping
    @ApiOperation(value = "批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result deleteByIds(@RequestParam List<Long> ids){
        log.info("删除套餐id:{}",ids);
        setMealService.deleteByIds(ids);
        return Result.success();
    }
    
    @PutMapping
    @ApiOperation(value = "更新套餐方法")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO){
        setMealService.update(setmealDTO);
        return Result.success();
    }
}

