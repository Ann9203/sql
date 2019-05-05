package com.example.sqldemo.db;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/3 17:50
 * 描述:
 */


public interface IBaseDAO<T> {
    long insert(T entity);
}
