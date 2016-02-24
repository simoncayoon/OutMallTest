package com.beetron.outmall;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beetron.outmall.customview.ViewWithBadge;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.DisplayMetrics;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.Indicator;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/3.
 * Time: 15:33.
 */
public class AboutMine extends BaseFragment {

    private static final String TAG = AboutMine.class.getSimpleName();
    Button testRegist;
    private RelativeLayout headView;
    private ImageView headImg;
    private TextView headName, headSign;
    private LinearLayout llOrderTab;
    private FixedIndicatorView scanTab;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.about_mine_layout);
        testRegist = (Button) findViewById(R.id.test_regist);
        testRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        initView();
    }

    private void initView() {
        headView = (RelativeLayout) findViewById(R.id.about_me_head_layout);
        headView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugFlags.logD(TAG, "去个人中心!");
            }
        });

        llOrderTab = (LinearLayout) findViewById(R.id.ll_to_order_scan);
        llOrderTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugFlags.logD(TAG, "去订单详情！");
            }
        });

        scanTab = (FixedIndicatorView) findViewById(R.id.about_me_order_scan_tab);

        final String[] scanTabName = new String[]{getResources().getString(R.string.order_waiting_pay), getResources().getString(R.string.order_waiting_grab),
                getResources().getString(R.string.order_have_accept), getResources().getString(R.string.order_delivery),
                getResources().getString(R.string.order_have_done)};
        final int[] drawableTop = new int[]{R.mipmap.user_card, R.mipmap.user_waiting, R.mipmap.user_receive, R.mipmap.user_distribution, R.mipmap.user_finish};
        scanTab.setAdapter(new Indicator.IndicatorAdapter() {
            @Override
            public int getCount() {
                return scanTabName.length;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.tab_main, parent, false);
                }
                ViewWithBadge textView = (ViewWithBadge) convertView.findViewById(R.id.tab_text_view);
                textView.setText(scanTabName[position]);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                textView.setTextColor(getResources().getColor(R.color.general_tab_text_color));
                Drawable top = getResources().getDrawable(drawableTop[position]);
                top.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(
                        getActivity(), 22), DisplayMetrics.dip2px(
                        getActivity(), 22)));

                textView.setCompoundDrawables(null, top, null, null);
                return convertView;
            }
        });
    }
}
