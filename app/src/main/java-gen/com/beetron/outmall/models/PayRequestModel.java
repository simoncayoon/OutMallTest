package com.beetron.outmall.models;

/**
 * Created by luomaozhong on 16/3/6.
 */
public class PayRequestModel {
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    private String orderid;
}
