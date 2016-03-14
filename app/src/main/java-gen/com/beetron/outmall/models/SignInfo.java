package com.beetron.outmall.models;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/3/14.
 * Time: 10:50.
 */
public class SignInfo {

    private String id;
    private String uid;
    private String date;
    private String jifen;

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getDate() {
        return date;
    }

    public String getJifen() {
        return jifen;
    }

    public void setId(String id) {

        this.id = id;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setJifen(String jifen) {
        this.jifen = jifen;
    }
}
