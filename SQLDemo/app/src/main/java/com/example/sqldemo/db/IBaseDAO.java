package com.example.sqldemo.db;

import java.util.List;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/3 17:50
 * 描述:
 */


public interface IBaseDAO<T> {
    /**
     * 添加
     * @param entity
     * @return
     */
    long insert(T entity);

    /**
     * 更新
     *
     * @param entity
     * @param where
     * @param flag      更新是and  / or
     * @return
     */
    long update(T entity, T where, boolean flag);

    /**
     * 删除数据
     * @param where
     * @return
     */
    int delete(T where, boolean flag);

    /**
     * 查询
     * @param where
     * @return
     */
    List<T> query(T where);

    /**
     * 查询
     * @param where
     * @param orderBy
     * @param startIndex
     * @param limit
     * @return
     */
    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);

}
