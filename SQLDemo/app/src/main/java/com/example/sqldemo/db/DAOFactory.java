package com.example.sqldemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.BaseAdapter;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/3 18:48
 * 描述: 数据工厂
 */


public class DAOFactory {
    private static DAOFactory daoFactory ;
    private SQLiteDatabase sqLiteDatabase ;
    protected Map<String, BaseDAO> map = Collections.synchronizedMap(new HashMap<String, BaseDAO>()); //线程同步


    public static DAOFactory getOurInstance(Context context){
        if (daoFactory == null){
            daoFactory = new DAOFactory(context);
        }

        return daoFactory;
    }

    protected DAOFactory(){}

//    sqliteDatabasePath="data/data/com.example.a48608.ls4_databaseframework_20180307/jett.db";
//    sqLiteDatabase=SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath,null);
    private DAOFactory(Context context){
        String path = null;
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED){
         path =Environment.getExternalStorageDirectory().getPath()+"/com.example.sqldemo.db/file/xue.db";
        } else {
            //     path = "/data/data/com.example.sqldemo/file/xue.db";
            path = context.getDatabasePath("xue.db").getPath();

        }
        Log.e("path", path);
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(path, null);    //创建数据库
    }


    public <M extends  BaseDAO<T>, T> M getBaseDAO( Class<T> entityClass, Class<M> daoClass){
        BaseDAO baseDAO = null;
        if (map.get(daoClass.getSimpleName()) != null){
            return (M) map.get(daoClass.getSimpleName());
        }
        try {
            baseDAO = daoClass.newInstance();
            baseDAO.init(sqLiteDatabase,entityClass);
            map.put(daoClass.getSimpleName(), baseDAO);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return (M) baseDAO;
    }

}
