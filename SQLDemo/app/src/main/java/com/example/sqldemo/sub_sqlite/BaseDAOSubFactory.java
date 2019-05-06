package com.example.sqldemo.sub_sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.sqldemo.db.BaseDAO;
import com.example.sqldemo.db.DAOFactory;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/6 23:24
 * 描述: 分庫
 */


public class BaseDAOSubFactory extends DAOFactory {
    private static BaseDAOSubFactory ourInstance = new BaseDAOSubFactory();

    public static BaseDAOSubFactory getOurInstance() {
        return ourInstance;
    }

    //定义数据库分库的数据库操作对象
    protected SQLiteDatabase sqLiteDatabase;

    protected BaseDAOSubFactory() {
    }


    public <M extends BaseDAO<T>, T> T getSubDao(Context context, Class<T> entityClass, Class<M> daoClass) {
        BaseDAO baseDAO = null;
        if (map.get(PrivateDataBasePath.getPath(context)) != null) {
            return (T) map.get(PrivateDataBasePath.getPath(context));
        }

        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(PrivateDataBasePath.getPath(context), null);
        try {
            baseDAO = daoClass.newInstance();
            baseDAO.insert(entityClass);
            map.put(PrivateDataBasePath.getPath(context), baseDAO);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return (T) baseDAO;
    }
}
