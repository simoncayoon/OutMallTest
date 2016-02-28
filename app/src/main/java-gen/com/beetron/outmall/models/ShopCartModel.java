package com.beetron.outmall.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/22.
 * Time: 10:50.
 */
public class ShopCartModel implements Parcelable {

    public static final Parcelable.Creator<ShopCartModel> CREATOR = new Parcelable.Creator<ShopCartModel>() {
        public ShopCartModel createFromParcel(Parcel in) {
            return new ShopCartModel(in);
        }

        public ShopCartModel[] newArray(int size) {
            return new ShopCartModel[size];
        }
    };
    private String id;
    private String sid;
    private String uid;
    private Integer num;
    private ShopCartDetail gs;
    private boolean isSelect;

    public ShopCartModel() {

    }

    private ShopCartModel(Parcel in) {
        this.id = in.readString();
        this.uid = in.readString();
        this.sid = in.readString();
        this.num = in.readInt();
        this.isSelect = in.readByte() == 1 ? true : false;
        this.gs = in.readParcelable(ShopCartDetail.class.getClassLoader());
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {

        this.isSelect = isSelect;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public ShopCartDetail getGs() {
        return gs;
    }

    public void setGs(ShopCartDetail gs) {
        this.gs = gs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(uid);
        dest.writeString(sid);
        dest.writeInt(num);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
        dest.writeParcelable(gs, PARCELABLE_WRITE_RETURN_VALUE);
    }
}
