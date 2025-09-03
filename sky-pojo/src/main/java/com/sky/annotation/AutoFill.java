package com.sky.annotation;
import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//指定注解可以在哪里使用
@Retention(value = RetentionPolicy.RUNTIME)//注解生命周期
public @interface AutoFill {
    //数据库操作类型
    OperationType value();
}
