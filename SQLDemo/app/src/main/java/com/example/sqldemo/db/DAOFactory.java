package com.example.sqldemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.BaseAdapter;

import java.lang.ref.WeakReference;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/3 18:48
 * 描述: 数据工厂
 */


public class DAOFactory {
    private static DAOFactory daoFactory ;
    private SQLiteDatabase sqLiteDatabase ;

    public static DAOFactory getOurInstance(Context context){
        if (daoFactory == null){
            daoFactory = new DAOFactory(context);
        }

        return daoFactory;
    }

    private DAOFactory(){}

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


    public <T> BaseDAO<T> getBaseDAO(Class<T> entityClass){
        BaseDAO baseDAO = null;
        try {
            baseDAO = BaseDAO.class.newInstance();
            baseDAO.init(sqLiteDatabase,entityClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return baseDAO;
    }

}
