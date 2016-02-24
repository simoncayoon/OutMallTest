package com.beetron.outmall.models;

import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/22.
 * Time: 10:23.
 */
public class ShopCartResult {

    private List<ShopCartModel> list;
    private Double zongjianshu;
    private Double zongjia;

    public List<ShopCartModel> getList() {
        return list;
    }

    public void setList(List<ShopCartModel> list) {
        this.list = list;
    }

    public Double getZongjianshu() {
        return zongjianshu;
    }

    public void setZongjianshu(Double zongjianshu) {
        this.zongjianshu = zongjianshu;
    }

    public Double getZongjia() {
        return zongjia;
    }

    public void setZongjia(Double zongjia) {
        this.zongjia = zongjia;
    }
}
