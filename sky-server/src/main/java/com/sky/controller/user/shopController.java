package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@RequiredArgsConstructor
@Api(tags = "店铺营业相关接口")
public class shopController {
    private final RedisTemplate redisTemplate;
    private static final String KEY = "SHOP_STATUS";
    
    @GetMapping("/status")
    @ApiOperation(value = "获取店铺营业状态")
    public Result<Integer> getStatus() {
        String status = (String) redisTemplate.opsForValue().get(KEY);
        log.info("店铺营业状态为{}", status.equals("1") ? "营业中" : "打样中");
        return Result.success(Integer.valueOf(status));
    }
}
