package com.example.sqldemo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.example.sqldemo.anontation.DataFields;
import com.example.sqldemo.anontation.DataTable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/3 17:52
 * 描述: 数据库基类
 */


public class BaseDAO<T> implements IBaseDAO<T> {

    private String tableName;
    private Class<T> entityClass;
    private SQLiteDatabase sqlDatabase;
    //定义一个缓存空间
    //key--字段名  value:成员变量
    private HashMap<String, Field> cacheMap;

    private boolean isInit = false;  //是否初始化

    public boolean init(SQLiteDatabase database, Class<T> entityClass) {
        this.entityClass = entityClass;
        this.sqlDatabase = database;

        if (!isInit) {
            //初始化创建表
            if (entityClass.getAnnotation(DataTable.class) != null && !TextUtils.isEmpty(tableName = entityClass.getAnnotation(DataTable.class).value())) {
                tableName = entityClass.getAnnotation(DataTable.class).value();
            } else {
                tableName = entityClass.getSimpleName();
            }

            //判断数据库是否连接
            if (!sqlDatabase.isOpen()) {
                return false;
            }

            //拼接创建数据库的字符串
            String createSQLStr = createSQL();
//            sqlDatabase.openOrCreateDatabase(createSQLStr, null);//创建表
            sqlDatabase.execSQL(createSQLStr);
            //存储缓存数据
            cacheMap = initCacheMap();
            isInit = true;

        }

        return isInit;
    }

    /**
     * 缓存数据
     *
     * @return
     */
    private HashMap<String, Field> initCacheMap() {
        HashMap<String, Field> map = new HashMap<>();
        String sqlStr = "select * from " + tableName + " limit 1.0";
        Cursor cursor = sqlDatabase.rawQuery(sqlStr, null);
        String[] columnNames = cursor.getColumnNames();
        //获取所有成员变量
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); //设置可访问权限
        }

        for (String columnName : columnNames) {
            Field columnField = null;
            for (Field field : fields) {
                String fileName;
                if (field.getAnnotation(DataFields.class) != null && !TextUtils.isEmpty(field.getAnnotation(DataFields.class).value())) {
                    fileName = field.getAnnotation(DataFields.class).value();
                } else {
                    fileName = field.getName();
                }
                if (columnName.endsWith(fileName)) {
                    columnField = field;
                    break;
                }
            }
            if (columnField != null){
                map.put(columnName, columnField);
            }
        }
        return map;

    }

    /**
     * 创建数据库的字符串
     *
     * @return
     */
    private String createSQL() {
        StringBuffer stringBuilder = new StringBuffer();
        stringBuilder.append("create table if not exists ");
        stringBuilder.append(tableName + "(");
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            Class type = field.getType(); //获取字段类型
            String valueName;
            if (field.getAnnotation(DataFields.class) != null && !TextUtils.isEmpty(field.getAnnotation(DataFields.class).value())) {
                //判断字段类型
                valueName = field.getAnnotation(DataFields.class).value();
            } else {
                valueName = field.getName();
                //判断字段类型
            }
            if (type == String.class) {
                //String类型
                stringBuilder.append(valueName).append(" TEXT,");
            } else if (type == Integer.class || type == int.class) {
                stringBuilder.append(valueName).append(" INTEGER,");
            } else if (type == Double.class || type == double.class) {
                stringBuilder.append(valueName).append(" DOUBLE,");
            } else if (type == Long.class || type == long.class) {
                stringBuilder.append(valueName).append(" BIGINT,");
            } else if (type == byte[].class) {
                stringBuilder.append(valueName).append(" BLOB,");

            } else {
                continue;
            }
        }
        if (stringBuilder.charAt(stringBuilder.length() - 1) == ',') {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append(")");
        return stringBuilder.toString();

    }


    /**
     * 插入语句经常用到 values 获取values
     * @return
     */
    private HashMap<String, String> getValues(T entity){
        HashMap<String, String>  map = new HashMap<>();
        Iterator<Field> fieldIterator = cacheMap.values().iterator();
        while (fieldIterator.hasNext()){
            Field field= fieldIterator.next();
            try {
                Object object = field.get(entity);
                if (object ==null){
                    continue;
                }
                String value = null;
                if (field.getAnnotation(DataFields.class) != null && !TextUtils.isEmpty(field.getAnnotation(DataFields.class).value())){
                     value = field.getAnnotation(DataFields.class).value();
                } else {
                    value = field.getName();
                }
                map.put(value, object.toString());

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return  map;
    }

    public ContentValues getContentValue(HashMap<String, String> map){
        ContentValues contentValues = new ContentValues();
        Set<String> keys = map.keySet();
        Iterator<String> keyIterator = keys.iterator();
        while (keyIterator.hasNext()){
            String key = keyIterator.next();
            String value = map.get(key);
            if (value != null){
                contentValues.put(key, value);
            }
        }
        return  contentValues;
    }

    @Override
    public long insert(T entity) {
        HashMap<String, String > value = getValues(entity);
        ContentValues contentValues = getContentValue(value);
        long result = sqlDatabase.insert(tableName, null, contentValues);
        return result;
    }



}
