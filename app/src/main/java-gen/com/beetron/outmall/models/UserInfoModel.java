package com.beetron.outmall.models;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/27.
 * Time: 11:48.
 */
public class UserInfoModel {

//    private String token;
//    private String isLogin;
//    private String uid;
    private String nickname;
    private String mail;
    private String sex;
    private String province;
    private String city;
    private String area;
    private String address;

//    public String getToken() {
//        return token;
//    }
//
//    public String getIsLogin() {
//        return isLogin;
//    }
//
//    public String getUid() {
//        return uid;
//    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }
//
//    public void setToken(String token) {
//
//        this.token = token;
//    }
//
//    public void setIsLogin(String isLogin) {
//        this.isLogin = isLogin;
//    }
//
//    public void setUid(String uid) {
//        this.uid = uid;
//    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
