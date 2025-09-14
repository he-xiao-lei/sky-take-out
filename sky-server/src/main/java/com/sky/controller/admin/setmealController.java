package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@RequiredArgsConstructor
@Slf4j
public class setmealController {
    private final SetmealService setMealService;
    
//    @GetMapping("/{id}")
//    public Result<SetmealVO> getSetMealById(@PathVariable Integer id) {
//        SetmealVO setmealVO = setmealService.getSetMealById(id);
//        log.info("套餐id为{}的信息{}", id, setmealVO);
//        return Result.success(setmealVO);
    
    
    @PostMapping("/status/{status}")
    @ApiOperation(value = "设置套餐状态")
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
    public Result save(@RequestBody SetmealDTO setmealDTO){
        setMealService.save(setmealDTO);
        return Result.success();
    }
    
    
    
}

