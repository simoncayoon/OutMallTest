package com.beetron.outmall.models;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/26.
 * Time: 13:00.
 */
public class OrderFixInfo {

    private AddrInfoModel address;
    private Double man;
    private Integer free;
    private Double fuwu;

    public AddrInfoModel getAddress() {
        return address;
    }

    public void setAddress(AddrInfoModel address) {

        this.address = address;
    }

    public Double getMan() {
        return man;
    }

    public void setMan(Double man) {
        this.man = man;
    }

    public Integer getFree() {
        return free;
    }

    public void setFree(Integer free) {
        this.free = free;
    }

    public Double getFuwu() {
        return fuwu;
    }

    public void setFuwu(Double fuwu) {
        this.fuwu = fuwu;
    }
}
