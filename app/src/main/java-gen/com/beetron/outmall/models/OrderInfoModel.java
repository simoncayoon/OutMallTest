package com.beetron.outmall.models;

import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/25.
 * Time: 14:43.
 */
public class OrderInfoModel {

    private static OrderInfoModel instance = null;

    private List<ShopCartModel> proDetail;
    private Double amount = 0.00;//商品总价
    private Double priceFree;//减免

    private OrderInfoModel() {

    }

    public static OrderInfoModel getInstance() {

        if (instance == null) {
            instance = new OrderInfoModel();
        }

        return instance;
    }

    public Double getPriceFree() {
        return priceFree;
    }

    public void setPriceFree(Double priceFree) {

        this.priceFree = priceFree;
    }

    public List<ShopCartModel> getProDetail() {
        return proDetail;
    }

    public void setProDetail(List<ShopCartModel> proDetail) {

        this.proDetail = proDetail;

        if (proDetail != null) {
            for (ShopCartModel shopCartModel : proDetail) {
                int count = Integer.valueOf(shopCartModel.getNum());
                Double price = Double.valueOf(shopCartModel.getGs().getPrice2());
                Double itemPrice = count * price;
                amount += itemPrice;
            }
        }
    }

    public Double getAmount() throws Exception {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeDouble(amount);
//        dest.writeList(proDetail);
//    }
//
//    public static final Parcelable.Creator<OrderInfoModel> CREATOR = new Parcelable.Creator<OrderInfoModel>() {
//        public OrderInfoModel createFromParcel(Parcel in) {
//            return new OrderInfoModel(in);
//        }
//
//        public OrderInfoModel[] newArray(int size) {
//            return new OrderInfoModel[size];
//        }
//    };
//
//    private OrderInfoModel(Parcel in){
//        amount = in.readDouble();
//    }
}
