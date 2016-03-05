package com.beetron.outmall.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/26.
 * Time: 11:57.
 */
public class OrderPostModel implements Parcelable{

    public static final Parcelable.Creator<OrderPostModel> CREATOR = new Parcelable.Creator<OrderPostModel>() {
        public OrderPostModel createFromParcel(Parcel in) {
            return new OrderPostModel(in);
        }

        public OrderPostModel[] newArray(int size) {
            return new OrderPostModel[size];
        }
    };
    private String id;
    private String token;
    private String uid;
    private String isLogin;
    private String address_id;
    private String payment;
    private String remark;
    private String zongjia;
    private String fuwufei;
    private String jianmian;
    private Double totalprice;
    private String goods;
    private String num;
    private String date;
    private String status;
    private String orderno;

    public OrderPostModel(){}

    private OrderPostModel(Parcel in){
        this.id = in.readString();
        this.token = in.readString();
        this.uid = in.readString();
        this.isLogin = in.readString();
        this.address_id = in.readString();
        this.payment = in.readString();
        this.remark = in.readString();
        this.zongjia = in.readString();
        this.fuwufei = in.readString();
        this.jianmian = in.readString();
        this.totalprice = in.readDouble();
        this.goods = in.readString();
        this.num = in.readString();
        this.date = in.readString();
        this.status = in.readString();
        this.orderno = in.readString();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {

        this.token = token;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {

        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(String isLogin) {
        this.isLogin = isLogin;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getZongjia() {
        return zongjia;
    }

    public void setZongjia(String zongjia) {
        this.zongjia = zongjia;
    }

    public String getFuwufei() {
        return fuwufei;
    }

    public void setFuwufei(String fuwufei) {
        this.fuwufei = fuwufei;
    }

    public String getJianmian() {
        return jianmian;
    }

    public void setJianmian(String jianmian) {
        this.jianmian = jianmian;
    }

    public Double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(Double totalprice) {
        this.totalprice = totalprice;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.token);
        dest.writeString(this.uid);
        dest.writeString(this.isLogin);
        dest.writeString(this.address_id);
        dest.writeString(this.payment);
        dest.writeString(this.remark);
        dest.writeString(this.zongjia);
        dest.writeString(this.fuwufei);
        dest.writeString(this.jianmian);
        dest.writeDouble(this.totalprice);
        dest.writeString(this.goods);
        dest.writeString(this.num);
        dest.writeString(this.date);
        dest.writeString(this.status);
        dest.writeString(this.orderno);
    }
}
