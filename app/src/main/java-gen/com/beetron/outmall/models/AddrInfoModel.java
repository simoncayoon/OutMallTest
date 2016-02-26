package com.beetron.outmall.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/24.
 * Time: 21:29.
 */
public class AddrInfoModel implements Parcelable {

//    "id": "2",
//            "uid": "1",
//            "name": "孔小方",
//            "mobile": "13312222855",
//            "sex": "1",
//            "shenfen": "1",
//            "shiqu": "1",
//            "address": "百花湖"

    public static final Parcelable.Creator<AddrInfoModel> CREATOR = new Parcelable.Creator<AddrInfoModel>() {
        public AddrInfoModel createFromParcel(Parcel in) {
            return new AddrInfoModel(in);
        }

        public AddrInfoModel[] newArray(int size) {
            return new AddrInfoModel[size];
        }
    };
    private String id;
    private String uid;
    private String name;
    private String mobile;
    private String sex;
    private String shenfen;
    private String shiqu;
    private String address;
    private String isLogin;
    private String token;

    public AddrInfoModel(){

    }

    private AddrInfoModel(Parcel in) {
        id = in.readString();
        uid = in.readString();
        name = in.readString();
        mobile = in.readString();
        sex = in.readString();
        shenfen = in.readString();
        shiqu = in.readString();
        address = in.readString();

    }

    public String getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(String isLogin) {
        this.isLogin = isLogin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {

        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getShenfen() {
        return shenfen;
    }

    public void setShenfen(String shenfen) {
        this.shenfen = shenfen;
    }

    public String getShiqu() {
        return shiqu;
    }

    public void setShiqu(String shiqu) {
        this.shiqu = shiqu;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(mobile);
        dest.writeString(sex);
        dest.writeString(shenfen);
        dest.writeString(shiqu);
        dest.writeString(address);
    }

}
