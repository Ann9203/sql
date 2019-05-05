package com.example.sqldemo.bean;

import com.example.sqldemo.anontation.DataFields;
import com.example.sqldemo.anontation.DataTable;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/3 17:48
 * 描述:
 */

@DataTable("tb_product")
public class Product {
    public int  productId;
    public String productName;
    public String dateTime;
    public String price;

    public Product(int productId, String productName, String dateTime, String price) {
        this.productId = productId;
        this.productName = productName;
        this.dateTime = dateTime;
        this.price = price;
    }
}
