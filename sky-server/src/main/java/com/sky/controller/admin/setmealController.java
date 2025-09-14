package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
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
    
    
    @PostMapping
    @ApiOperation(value = "新增套餐方法")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        setMealService.save(setmealDTO);
        return Result.success();
    }
    
    
    
}

