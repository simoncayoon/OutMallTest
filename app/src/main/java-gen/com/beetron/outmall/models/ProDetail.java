package com.beetron.outmall.models;

import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/19.
 * Time: 14:37.
 */
public class ProDetail {

    private String sid;
    private String fid;
    private String title;
    private String miaoshu;
    private String tuwen;
    private List<ProDetailPic> img;
    private Double price1;
    private Double price2;
    private Integer xiaoliang;

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

    public String getMiaoshu() {
        return miaoshu;
    }

    public void setMiaoshu(String miaoshu) {
        this.miaoshu = miaoshu;
    }

    public String getTuwen() {
        return tuwen;
    }

    public void setTuwen(String tuwen) {
        this.tuwen = tuwen;
    }

    public List<ProDetailPic> getImg() {
        return img;
    }

    public void setImg(List<ProDetailPic> img) {
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

    public Integer getXiaoliang() {
        return xiaoliang;
    }

    public void setXiaoliang(Integer xiaoliang) {
        this.xiaoliang = xiaoliang;
    }

    public class ProDetailPic{
        String pic;

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {

            this.pic = pic;
        }
    }
}
