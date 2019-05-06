package com.example.sqldemo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.example.sqldemo.anontation.DataFields;
import com.example.sqldemo.anontation.DataTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

    protected boolean init(SQLiteDatabase database, Class<T> entityClass) {
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
                if (columnName.equals(fileName)) {
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
            if (valueName.equals("serialVersionUID")){
                continue;
            }
            if (type == String.class) {
                //String类型
                stringBuilder.append(valueName).append(" TEXT,");
            } else if (type == Integer.class ) {
                stringBuilder.append(valueName).append(" INTEGER,");
            } else if (type == Double.class ) {
                stringBuilder.append(valueName).append(" DOUBLE,");
            } else if (type == Long.class ) {
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
    private Map<String, String> getValues(T entity){
        Map<String, String>  map = new HashMap<>();
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

    public ContentValues getContentValue(Map<String, String> map){
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
        Map<String, String > value = getValues(entity);
        ContentValues contentValues = getContentValue(value);
        long result = sqlDatabase.insert(tableName, null, contentValues);
        return result;
    }

    /**
     * 更新数据
     * @param entity
     * @param where
     * @return
     */
    @Override
    public long update(T entity, T where, boolean flag) {

        Map<String, String> map = getValues(entity);
        ContentValues contentValues = getContentValue(map);

        Map<String, String > whereMap = getValues(where);
        Condition condition = new Condition(whereMap, flag);
        //封装 "name= ? and .."
         int  result =   sqlDatabase.update(tableName, contentValues, condition.whereCause, condition.whereArgs);
        return result;
    }

    private class Condition{
        private String whereCause; //选择条件
        private String[] whereArgs; //选择对应数值

        /**
         * 传入值
         * @param map
         */
        public Condition(Map<String, String> map, boolean flag){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("1=1");
           Set<String> keySet = map.keySet();
           Iterator<String> keyIterator = keySet.iterator();
            ArrayList list = new ArrayList();
           while (keyIterator.hasNext()){
               String key = keyIterator.next(); //"name"
               String value = map.get(key); //"lixue"
               if (!TextUtils.isEmpty(value)){
                  stringBuilder.append(flag? " and " + key +" = ?" : " or "+key+" =?");
                  list.add(value);
               }
           }
           this.whereCause = stringBuilder.toString();
          this.  whereArgs = (String[]) list.toArray(new String[list.size()]);
        }
    }

    /**
     * 删除数据
     * @param where
     * @return
     */
    @Override
    public int delete(T where, boolean flag) {
        Map<String, String> map = getValues(where);
        Condition condition =  new Condition(map, flag);
       int result =  sqlDatabase.delete(tableName, condition.whereCause, condition.whereArgs);
        return result;
    }

    /**
     * 查询数据
     * @param where
     * @return
     */
    @Override
    public List<T> query(T where) {
     return    query(where, null, null, null);
    }

    /**
     * 查询数据
     * @param where
     * @param orderBy
     * @param startIndex
     * @param limit
     * @return
     */
    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
   //    sqLiteDatabase.query(tableName,null,"id=?",new String[],null,null,orderBy,"1,5");

        Map map = getValues(where);
        Condition condition = new Condition(map, true); //都需要flag值 省略
        String limitStr = null;
        if (startIndex != null && limit != null){
            limitStr = startIndex +" , "+limit;
        }
        Cursor cursor = sqlDatabase.query(tableName,null, condition.whereCause, condition.whereArgs, null, null, orderBy, limitStr );
        //解析cureor 返回List<T>
        List<T> result = getResult(where,  cursor);
        return result;
    }

    private List<T> getResult(T obj, Cursor cursor){

        List<T> list = new ArrayList<>();
        Object item = null;
        while (cursor.moveToNext()){
            try {
                item = obj.getClass().newInstance();
                Iterator iterator = cacheMap.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry entry = (Map.Entry) iterator.next();
                    //获取列名
                    String  columnName = (String) entry.getKey();
                    int index = cursor.getColumnIndex(columnName);
                    //然后以列名拿到游标的位置
                    Field field = (Field) entry.getValue();
                    Class type = field.getType();
                    if (type== Integer.class){
                        field.set(item, cursor.getInt(index));
                    } else if (type == String.class){
                        field.set(item, cursor.getString(index));
                    } else if ( type == Double.class){
                        field.set(item, cursor.getDouble(index));
                    } else if (type == Long.class){
                        field.set(item, cursor.getLong(index));
                    } else if (type == byte[].class){
                        field.set(item, cursor.getBlob(index));
                    } else {
                        //不支持
                        continue;
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            if (item != null){
                list.add((T) item);
            }
        }

        return list;
    }


}
