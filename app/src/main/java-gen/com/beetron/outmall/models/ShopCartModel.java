package com.beetron.outmall.models;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/22.
 * Time: 10:50.
 */
public class ShopCartModel {

    private String id;
    private String sid;
    private String uid;
    private String num;
    private ShopCartDetail gs;
    private boolean isSelect;

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

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public ShopCartDetail getGs() {
        return gs;
    }

    public void setGs(ShopCartDetail gs) {
        this.gs = gs;
    }

    public class ShopCartDetail {
        private String title;
        private  String img;
        private String jianshu;
        private String price1;
        private String price2;
        private  String cuxiao;
        private String xiangou;

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

        public String getPrice1() {
            return price1;
        }

        public void setPrice1(String price1) {
            this.price1 = price1;
        }

        public String getPrice2() {
            return price2;
        }

        public void setPrice2(String price2) {
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
    }
}
