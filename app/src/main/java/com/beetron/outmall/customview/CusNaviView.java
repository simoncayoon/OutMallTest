package com.beetron.outmall.customview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beetron.outmall.R;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.DisplayMetrics;


/**
 * Created by apple on 15/4/1.
 */
public class CusNaviView extends RelativeLayout {

    public static final String PUT_LEFT = "LEFT_FLAG";
    public static final String PUT_RIGHT = "RIGHT_FLAG";
    public static final String PUT_BACK_ENABLE = "BACK_FLAG";
    public static final int NAVI_WRAP_CONTENT = 0;
    private static final String TAG = CusNaviView.class.getSimpleName();
    private Context mContext;

    private TextView naviTitle;
    private View tempBtn;

    private View leftBtn;
    private View rightBtn;

    private NaviBtnListener callBack = new NaviBtnListener() {

        @Override
        public void rightBtnListener() {
        }

        @Override
        public void leftBtnListener() {
        }
    };
    private NaviBtnListener naviBtnListener = callBack;

    public CusNaviView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.general_navigation_layout, this);
        naviTitle = (TextView) findViewById(R.id.general_navi_title_view);
        if (context instanceof NaviBtnListener) {
            naviBtnListener = (NaviBtnListener) context;
        } else {
            DebugFlags.logD(TAG, "context not implements NaviBtnListener");
        }

    }

    public void setNaviTitle(String naviText) {
        this.naviTitle.setText(naviText);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        naviBtnListener = callBack;
    }

    /**
     * 设置功能按钮
     *
     * @param flag 按钮位置flag
     */
    public void setBtn(String flag, int width, int height) {

        Button newButton = new Button(mContext);

        if(flag.equals(PUT_BACK_ENABLE)){
            Drawable leftBack = getResources().getDrawable(R.mipmap.nav_ic_back);
            leftBack.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(
                    mContext, 20), DisplayMetrics.dip2px(
                    mContext, 20)));
            newButton.setTextColor(getResources().getColor(R.color.general_main_title_color));
            newButton.setCompoundDrawables(leftBack, null, null, null);
            newButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        naviBtnListener.leftBtnListener();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        newButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        // set button drawable
        newButton.setBackgroundColor(Color.parseColor("#00000000"));
        setBtnView(flag, newButton, width, height);

    }

    public void setBtnView(String flag, View view, int width, int height) {
        // set LayoutParams
        LayoutParams lp = null;
        if (width == NAVI_WRAP_CONTENT) {
            lp = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics()));
        } else {
            lp = new LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics()));
        }

        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
        // set button drawable
        view.setBackgroundColor(Color.parseColor("#00000000"));
        if(flag.equals(PUT_BACK_ENABLE)){
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//设在左边
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        naviBtnListener.leftBtnListener();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            setLeftBtn(view);
        }else if (flag.equals(PUT_LEFT)) {//左按钮

            try {
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//设在左边
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        naviBtnListener.leftBtnListener();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            setLeftBtn(view);
        }else if (flag.equals(PUT_RIGHT)) {//右按钮
            lp.addRule(RelativeLayout.CENTER_VERTICAL);
            lp.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
            try {
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//设在右边
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        naviBtnListener.rightBtnListener();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            setRightBtn(view);
        }
        view.setLayoutParams(lp);
        view.setTag(flag);
        tempBtn = view;
        // add button
        this.addView(view);
    }


    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setNaviBtnListener(NaviBtnListener listener) {
        naviBtnListener = listener;
    }

    public View getLeftBtn() {
        return leftBtn;
    }

    private void setLeftBtn(View leftBtn) {
        this.leftBtn = leftBtn;
    }

    public View getRightBtn() {
        return rightBtn;
    }

    private void setRightBtn(View rightBtn) {
        this.rightBtn = rightBtn;
    }

    public void removeBtn(String flag){

//        for (int index = 0; index < this.getChildCount(); index ++);{
//
//        }
        try {
            if (PUT_LEFT.equals(flag) || PUT_BACK_ENABLE.equals(flag)) {
                this.removeView(getLeftBtn());
            } else {
                this.removeView(getRightBtn());
//                this.remove
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 回调接口
     */
    public interface NaviBtnListener {
        /**
         * 左按钮监听器
         */
        void leftBtnListener();

        /**
         * 右按钮监听器
         */
        void rightBtnListener();
    }
}
