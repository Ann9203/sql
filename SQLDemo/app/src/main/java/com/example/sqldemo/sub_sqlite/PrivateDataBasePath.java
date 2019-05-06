package com.example.sqldemo.sub_sqlite;

import android.content.Context;
import android.os.Environment;

import com.example.sqldemo.bean.User;
import com.example.sqldemo.db.DAOFactory;

import java.io.File;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/6 23:54
 * 描述: 存放私有数据库
 */


public class PrivateDataBasePath {

    /**
     * 获取数据库路径
     * @param context
     * @return
     */
    public static String getPath(Context context){
        UserDao userDao = DAOFactory.getOurInstance(context).getBaseDAO(User.class, UserDao.class);
        if (userDao != null){
            User currentUser = userDao.getCurrentUser();
            String path ;
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED){
                path =Environment.getExternalStorageDirectory().getPath()+"/com.example.sqldemo.db/file/xue.db";
            } else {
                //     path = "/data/data/com.example.sqldemo/file/xue.db";
                path = context.getDatabasePath("xue.db").getPath();

            }
            if (currentUser != null){
                File file = new File(path);
                if (!file.exists()){
                    file.mkdir();
                }
                return file.getAbsolutePath()+"/"+currentUser.getId()+"_login.db";
            }
        }
        return null;
    }
}
