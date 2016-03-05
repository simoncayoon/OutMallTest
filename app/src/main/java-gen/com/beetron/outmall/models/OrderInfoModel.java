package com.beetron.outmall.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/25.
 * Time: 14:43.
 */
public class OrderInfoModel {

    private static OrderInfoModel instance = null;

    private List<ProSummary> proDetail;
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

    public List<ProSummary> getProDetail() {
        return proDetail;
    }

    public void setProDetail(List<ProSummary> proDetail) {

        if (proDetail != null) {
            for (ProSummary shopCartModel : proDetail) {
                int count = Integer.valueOf(shopCartModel.getCount());
                Double price = Double.valueOf(shopCartModel.getPrice2());
                Double itemPrice = count * price;
                amount += itemPrice;
            }
            this.proDetail = proDetail;
        } else {
            amount = 0.00;
            this.priceFree= 0.00;
            this.proDetail = new ArrayList<ProSummary>();
        }
    }

    public Double getAmount() throws Exception {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
