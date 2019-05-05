package com.example.sqldemo.anontation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/3 17:29
 * 描述: 给类的注解
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataTable {
     String value();
}
