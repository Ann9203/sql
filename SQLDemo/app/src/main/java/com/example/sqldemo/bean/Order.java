package com.example.sqldemo.bean;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/3 17:49
 * 描述:
 */


public class Order {
    public int orderId;
    public String orderNumber;
    public String des;

    public Order(int orderId, String orderNumber, String des) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.des = des;
    }
}
