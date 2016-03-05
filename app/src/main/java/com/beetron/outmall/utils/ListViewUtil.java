package com.beetron.outmall.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/3/5.
 * Time: 17:31.
 */
public class ListViewUtil {

    private static final Integer MAX_ITEM_LINES = 3;

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        if (listAdapter.getCount() > MAX_ITEM_LINES){
            View listItem = listAdapter.getView(0, null, listView);//获取第一项的View
            listItem.measure(0, 0);
            totalHeight = listItem.getMeasuredHeight() * MAX_ITEM_LINES;
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void setListViewHeightBasedOnChildrenT(ListView listView) {

        // 获取ListView对应的Adapter

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {

            return;

        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目

            View listItem = listAdapter.getView(i, null, listView);

            listItem.measure(0, 0); // 计算子项View 的宽高

            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        // listView.getDividerHeight()获取子项间分隔符占用的高度

        // params.height最后得到整个ListView完整显示需要的高度

        listView.setLayoutParams(params);

    }
}
