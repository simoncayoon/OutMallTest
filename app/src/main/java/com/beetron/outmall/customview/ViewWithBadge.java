package com.beetron.outmall.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.beetron.outmall.utils.DebugFlags;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/15.
 * Time: 17:39.
 */
public class ViewWithBadge extends TextView {

    private static final String TAG = ViewWithBadge.class.getSimpleName();
    private BadgeView badgeView = null;//气泡
    private BadgeViewLeft badgeViewleft = null;//气泡
    private Context context;
    private int baseNum = 1;

    public ViewWithBadge(Context context) {
        this(context, null);
    }

    public ViewWithBadge(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewWithBadge(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {

    }

    /**
     * 设置气泡
     * @param position 气泡位置
     * @param count 气泡显示内容
     * @param textSize 气泡字体大小
     * @param margin 气泡边界
     */
    public void setBadge(int position, Integer count, int textSize, int margin) {

        if (position == BadgeView.POSITION_TOP_RIGHT) {
            if (badgeView == null) {
                badgeView = new BadgeView(context, this);//物候绑定数量气泡
                if (count > 0) {
                    try {
                        badgeView.setText(String.valueOf(count));//设置数量
                    } catch (Exception e) {
                        e.printStackTrace();
                        DebugFlags.logD(TAG, "请填充数字！！");
                    }
                    badgeView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getResources().getDisplayMetrics()));
                    badgeView.setBadgePosition(position);//设置气泡位置
                    badgeView.setBadgeMargin(margin);//
                    badgeView.show();
                } else if (count == 0) {
                    if (badgeView.isShown()){
                        badgeView.setVisibility(View.GONE);//
                        badgeView = null;//回收
                    }
                }
            }
        } else if(position == BadgeView.POSITION_TOP_LEFT) {
            if (badgeViewleft == null) {
                badgeViewleft = new BadgeViewLeft(context, this);//物候绑定数量气泡
            }
            if (count > 0) {
                try {
                    badgeViewleft.setText(String.valueOf(count));//设置数量
                } catch (Exception e) {
                    e.printStackTrace();
                    DebugFlags.logD(TAG, "请填充数字！！");
                }
                badgeViewleft.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getResources().getDisplayMetrics()));
                badgeViewleft.setBadgePosition(position);//设置气泡位置
                badgeViewleft.setBadgeMargin(margin);//
                badgeViewleft.show();
            } else if (count == 0) {
                if (badgeViewleft.isShown()){
                    badgeViewleft.setVisibility(View.GONE);//
                    badgeViewleft = null;//回收
                }
            }
        }

//
//        if (count > 0) {
//            try {
//                badgeView.setText(String.valueOf(count));//设置数量
//            } catch (Exception e) {
//                e.printStackTrace();
//                DebugFlags.logD(TAG, "请填充数字！！");
//            }
//            badgeView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getResources().getDisplayMetrics()));
//            badgeView.setBadgePosition(position);//设置气泡位置
//            badgeView.setBadgeMargin(margin);//
//            badgeView.show();
//        } else if (count == 0) {
//            if (badgeView.isShown()){
//                badgeView.setVisibility(View.GONE);//
//                badgeView = null;//回收
//            }
//        }
    }


    public BadgeView getPheBadgeView() {
        return badgeView;
    }

    public void setPheBadgeView(BadgeView pheBadgeView) {
        this.badgeView = pheBadgeView;
    }

    public int getBaseNum() {
        return baseNum;
    }

    public void setBaseNum(int baseNum) {
        this.baseNum = baseNum;
    }
}
