package com.beetron.outmall.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/25.
 * Time: 14:33.
 */
public class RemarkListView extends HorizontalScrollView implements
        RemarkListInterface {

    @SuppressWarnings("unused")
    private static final String TAG = "RemarkListView";
    private RemarkItemContainer itemContainer;
    private DataSetObserver dataSetObserver = new DataSetObserver() {

        @Override
        public void onChange() {

        }
    };

    public RemarkListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        itemContainer = new RemarkItemContainer(context);
        addView(itemContainer, new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT));
        setHorizontalScrollBarEnabled(false);
    }

    @Override
    public void onItemSelectListener() {

    }

    @Override
    public RemarkNameAdapter getAdapter() {
        return itemContainer.getAdapter();
    }

    @Override
    public void setAdapter(RemarkNameAdapter adapter) {

        if (getAdapter() != null) {
            getAdapter().unRegistDataSetObserver(dataSetObserver);
        }
        itemContainer.setAdapter(adapter);
        adapter.registDataSetObserver(dataSetObserver);
    }

    @Override
    public void setOnItemSelectListener(
            OnItemSelectedListener onItemSelectedListener) {
        itemContainer.setOnItemSelectListener(onItemSelectedListener);

    }
}