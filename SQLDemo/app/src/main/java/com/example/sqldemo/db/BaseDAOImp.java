package com.example.sqldemo.db;

import java.util.List;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/6 14:26
 * 描述:  可扩展的BaseDAO
 */


public class BaseDAOImp<T> extends  BaseDAO<T> {
    public List<T> query (String sql){
        return  null;
    }
}
