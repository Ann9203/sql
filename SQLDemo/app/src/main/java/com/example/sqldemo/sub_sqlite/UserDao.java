package com.example.sqldemo.sub_sqlite;

import com.example.sqldemo.bean.User;
import com.example.sqldemo.db.BaseDAO;

import java.util.List;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/6 23:25
 * 描述: 管理登录
 */


public class UserDao extends BaseDAO<User> {
    @Override
    public long insert(User entity) {
        List<User>  list =  query(new User());
        User where = null;
        for (User user: list){
            where = new User();
            where.setId(user.getId());
            user.setStatus(0);
            update(user, where, true);
        }
        entity.setStatus(0);

        return super.insert(entity);

    }

    /**
     * 获取UserInFo
     * @return
     */
    public User getCurrentUser(){
        User user = new User();
        user.setStatus(1);
        List<User> list = query(user);
        if (list.size() >= 1){
            return list.get(0);
        }
        return null;
    }
}
