package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@RequiredArgsConstructor
@Api(tags = "店铺营业相关接口")
public class shopController {
    private final RedisTemplate redisTemplate;
    
    private static final String KEY = "SHOP_STATUS";
    
    @PutMapping("/{status}")
    @ApiOperation(value = "设置店铺营业状态")
    public Result setStatus(@PathVariable String status) {
        
        log.info("设置店铺营业状态为{}", status.equals("1") ? "营业中" : "打样中");
        redisTemplate.opsForValue().set(KEY, status);
        
        return Result.success();
    }
    
    @GetMapping("/status")
    @ApiOperation(value = "获取店铺营业状态")
    public Result<Integer> getStatus() {
        String status = (String) redisTemplate.opsForValue().get(KEY);
        log.info("店铺营业状态为{}", status.equals("1") ? "营业中" : "打样中");
        return Result.success(Integer.valueOf(status));
    }
}
