package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user/user")//用户端用户模块
@RequiredArgsConstructor
@Api(tags = "C端用户相关接口")
public class UserController {
    private final UserService userService;
    private final JwtProperties jwtProperties;
    
    @PostMapping("/login")
    @ApiOperation(value = "用户登录接口")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) throws Exception {
        log.info("微信用户登录授权码{}",userLoginDTO);
        // 用户登录
        User user = userService.wxLogin(userLoginDTO);
        // 给微信用户生成jwt令牌
        Map<String, Object> claim = new HashMap<>();
        claim.put(JwtClaimsConstant.USER_ID,user.getId());
        String jwt = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claim);
        
        UserLoginVO build = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(jwt)
                .build();
        return Result.success(build);
    }
    


}
