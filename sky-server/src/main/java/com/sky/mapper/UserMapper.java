package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    
    
    User getUserByOpenId(String openId);
    void insert(User build);
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);
    
    /**
     * 根据动态条件统计用户数量
      * @return 用户数量
     */
    Integer countByMap(Map map);
}
