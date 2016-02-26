package com.beetron.outmall.customview;

import java.util.LinkedHashSet;
import java.util.Set;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/25.
 * Time: 14:34.
 */
public interface RemarkListInterface {

    public void onItemSelectListener();

    public RemarkNameAdapter getAdapter();

    public void setAdapter(RemarkNameAdapter adapter);

    /**
     * 设置选中监听
     *
     * @param onItemSelectedListener
     */
    public void setOnItemSelectListener(OnItemSelectedListener onItemSelectedListener);

    /**
     *
     * @author DKY
     */
    static interface DataSetObserver {
        public void onChange();
    }

    public static interface OnItemSelectedListener {
        /**
         * 注意 preItem 可能为 -1。表示之前没有选中过,每次adapter.notifyDataSetChanged也会将preItem
         * 设置为-1；
         *
         * @param selectItemView
         *            当前选中的view
         * @param select
         *            当前选中项的索引
         */
        public void onItemSelected(View selectItemView, int select);
    }

    /**
     * 适配器
     *
     */
    public static abstract class RemarkNameAdapter {
        private Set<DataSetObserver> observers = new LinkedHashSet<RemarkListInterface.DataSetObserver>(
                2);

        public abstract int getCount();

        public abstract Object getItem(int position);

        public abstract View getView(int position, View convertView,
                                     ViewGroup parent);

        public void notifyDataSetChanged() {
            for (DataSetObserver dataSetObserver : observers) {
                dataSetObserver.onChange();
            }
        }

        public void registDataSetObserver(DataSetObserver observer) {
            observers.add(observer);
        }

        public void unRegistDataSetObserver(DataSetObserver observer) {
            observers.remove(observer);
        }

    }
}
