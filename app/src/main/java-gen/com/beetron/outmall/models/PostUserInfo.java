package com.beetron.outmall.models;

/**
 * Created by luomaozhong on 16/3/1.
 */
public class PostUserInfo {

    private String token;
    private String nickname;//分类ID
    private String mail;//页码
    private String sex;//每页显示条数
    private String province;//商品ID
    private String city;//商品ID
    private String area;//商品ID
    private String address;//商品ID
    private String isLogin;//（1是已登录，0是未登录）
    private String uid;
    private String img;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {

        this.uid = uid;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {

        this.img = img;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {

        this.token = token;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(String isLogin) {
        this.isLogin = isLogin;
    }

    public String getProvince() {
        return province;
    }

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
