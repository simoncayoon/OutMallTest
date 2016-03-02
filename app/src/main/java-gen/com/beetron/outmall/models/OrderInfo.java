package com.beetron.outmall.models;

/**
 * Created by luomaozhong on 16/3/1.
 */
public class OrderInfo {

    private String token;
    private String uid;
    private String isLogin;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getRefer() {
        return isLogin;
    }

    public void setIsLogin(String isLogin) {
        this.isLogin = isLogin;
    }

}
