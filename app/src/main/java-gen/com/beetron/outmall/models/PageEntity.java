package com.beetron.outmall.models;

import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/15.
 * Time: 13:23.
 */
public class PageEntity<T> {

    private List<T> list;
    private int page;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {

        this.list = list;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
