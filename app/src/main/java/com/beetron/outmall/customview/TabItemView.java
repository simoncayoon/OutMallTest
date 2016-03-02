package com.beetron.outmall.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beetron.outmall.R;

/**
 * Created by luomaozhong on 16/2/29.
 */
public class TabItemView extends RelativeLayout {

    private TextView tv_title;
    private TextView tv_line;

    public TabItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 将自定义组合控件的布局渲染成View
        View view = View.inflate(context, R.layout.table_item, this);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_line = (TextView) view.findViewById(R.id.tv_line);

    }

    /**
     * 设置是否显示线条
     *
     * @return
     */
    public void hiddenLine() {
        tv_line.setVisibility(View.GONE);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setText(String title, int color) {
        tv_title.setText(title);
        tv_title.setTextColor(getResources().getColor(color));
    }

}