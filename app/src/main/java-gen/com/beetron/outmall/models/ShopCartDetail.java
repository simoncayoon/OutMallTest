package com.beetron.outmall.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/25.
 * Time: 15:26.
 */
public class ShopCartDetail implements Parcelable {
    public static final Parcelable.Creator<ShopCartDetail> CREATOR = new Parcelable.Creator<ShopCartDetail>() {
        public ShopCartDetail createFromParcel(Parcel in) {
            return new ShopCartDetail(in);
        }

        public ShopCartDetail[] newArray(int size) {
            return new ShopCartDetail[size];
        }
    };

    private String fid;
    private String title;
    private String img;
    private String jianshu;
    private Double price1;
    private Double price2;
    private String cuxiao;
    private String xiangou;
    private Double tejia;
    private String cxlx;

    public ShopCartDetail() {

    }

    private ShopCartDetail(Parcel in) {
        this.title = in.readString();
        this.img = in.readString();
        this.jianshu = in.readString();
        this.price1 = in.readDouble();
        this.price2 = in.readDouble();
        this.cuxiao = in.readString();
        this.xiangou = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getJianshu() {
        return jianshu;
    }

    public void setJianshu(String jianshu) {
        this.jianshu = jianshu;
    }

    public Double getPrice1() {
        return price1;
    }

    public void setPrice1(Double price1) {
        this.price1 = price1;
    }

    public Double getPrice2() {
        return price2;
    }

    public void setPrice2(Double price2) {
        this.price2 = price2;
    }

    public String getCuxiao() {
        return cuxiao;
    }

    public void setCuxiao(String cuxiao) {
        this.cuxiao = cuxiao;
    }

    public String getXiangou() {
        return xiangou;
    }

    public void setXiangou(String xiangou) {
        this.xiangou = xiangou;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {

        this.fid = fid;
    }

    public Double getTejia() {
        return tejia;
    }

    public void setTejia(Double tejia) {
        this.tejia = tejia;
    }

    public String getCxlx() {
        return cxlx;
    }

    public void setCxlx(String cxlx) {
        this.cxlx = cxlx;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.img);
        dest.writeString(this.jianshu);
        dest.writeDouble(this.price1);
        dest.writeDouble(this.price2);
        dest.writeString(this.cuxiao);
        dest.writeString(this.xiangou);
    }
}
