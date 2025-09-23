package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    // 微信服务接口地址
    
    private static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    private final UserMapper userMapper;
    private final WeChatProperties weChatProperties;
    
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) throws Exception {
        String openid = getOpenid(userLoginDTO);
        
        // 如果为空抛出业务异常
        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 判断用户openid是否在我数据库中，否则就是新用户
        User user = userMapper.getUserByOpenId(openid);
        if (user == null) {
            // 如果是新用户，则自动完成注册
            // 手动构建用户信息插入数据库
            user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
            userMapper.insert(user);
        }
        
        
        return user;
    }
    
    private String getOpenid(UserLoginDTO userLoginDTO) {
        // 调用微信接口,获取当前用户openid
        // 构建请求体
        Map<String, String> body = Map.of("appid", weChatProperties.getAppid(), "secret", weChatProperties.getSecret(), "js_code", userLoginDTO.getCode(), "grant_type", "authorization_code");
        // 发送请求，返回请求体字符串
        String response = HttpClientUtil.doGet(WX_LOGIN, body);
        // 判断openid是否为空,如果为空就是获取失败,不会空，则是一个合法的微信用户
        
        
        
        JSONObject jsonObject = JSONObject.parseObject(response);
        // 返回openid
        return (String) jsonObject.get("openid");
    }
}
