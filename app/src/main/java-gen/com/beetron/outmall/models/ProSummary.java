package com.beetron.outmall.models;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "PRO_SUMMARY".
 */
public class ProSummary {

    private String sid;
    private String fid;
    private String title;
    private String jianshu;
    private String img;
    private Double price1;
    private Double price2;
    private Integer xl;
    private Integer count;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public ProSummary() {
    }

    public ProSummary(String sid, String fid, String title, String jianshu, String img, Double price1, Double price2, Integer xl, Integer count) {
        this.sid = sid;
        this.fid = fid;
        this.title = title;
        this.jianshu = jianshu;
        this.img = img;
        this.price1 = price1;
        this.price2 = price2;
        this.xl = xl;
        this.count = count;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJianshu() {
        return jianshu;
    }

    public void setJianshu(String jianshu) {
        this.jianshu = jianshu;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public Integer getXl() {
        return xl;
    }

    public void setXl(Integer xl) {
        this.xl = xl;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    // KEEP METHODS - put your custom methods here

    public void setCount(Integer count) {
        this.count = count;
    }
    // KEEP METHODS END

}
