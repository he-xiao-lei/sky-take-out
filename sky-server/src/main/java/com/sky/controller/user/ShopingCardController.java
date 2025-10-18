package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.result.Result;
import com.sky.service.ShopingCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@Controller
@RestController("/user/shopingCard")
@Api("C端-购物车相关接口")
@RequiredArgsConstructor
public class ShopingCardController {
    private final ShopingCardService shopingCardService;
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    /**
     * 添加购物车
     */
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车:{}", shoppingCartDTO);
        shopingCardService.add(shoppingCartDTO);
        return Result.success();
    }
}
