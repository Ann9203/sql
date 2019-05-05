package com.example.sqldemo.bean;

import com.example.sqldemo.anontation.DataFields;
import com.example.sqldemo.anontation.DataTable;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/3 17:44
 * 描述: 用户类
 */

@DataTable("tb_user")
public class User {
    @DataFields("_id")
    public int id;
    public String name;
    public  int  sex;
    public String address;

    public User(int id, String name, int sex, String address) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.address = address;
    }
}
