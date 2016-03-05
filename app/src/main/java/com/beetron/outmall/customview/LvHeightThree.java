package com.beetron.outmall.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/3/5.
 * Time: 17:21.
 */
public class LvHeightThree extends ListView {
    public LvHeightThree(Context context) {
        super(context);
    }

    public LvHeightThree(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LvHeightThree(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
