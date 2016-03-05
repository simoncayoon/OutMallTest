package com.beetron.outmall;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beetron.outmall.customview.ViewWithBadge;
import com.beetron.outmall.models.UserInfoModel;
import com.beetron.outmall.utils.DBHelper;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.DisplayMetrics;
import com.beetron.outmall.utils.TempDataManager;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.Indicator;
import com.squareup.picasso.Picasso;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/3.
 * Time: 15:33.
 */
public class AboutMine extends BaseFragment {

    public static final int INTENT_FLAG_LOGIN_REQ = 0x11;
    private static final String TAG = AboutMine.class.getSimpleName();
    private RelativeLayout headView;
    private RoundedImageView headImg;
    private TextView headName;
    private LinearLayout llOrderTab;
    private FixedIndicatorView scanTab;
    private TempDataManager tempDataManager;
    private UserInfoModel memberModel;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.about_mine_layout);

        tempDataManager = TempDataManager.getInstance(getActivity());
        initView();

        initUserInfo();
    }

    /***
     * 设置用户昵称
     */
    private void initUserInfo() {
        try {
            if (TempDataManager.getInstance(getApplicationContext()).isLogin()) {
                Log.d("AboutMine","已登录");
                memberModel = DBHelper.getInstance(getActivity()).getUserInfo();
                Log.d("AboutMine","用户头像："+memberModel.getHeadimg());
                Picasso.with(getActivity())
                        .load(memberModel.getHeadimg())
                        .placeholder(R.mipmap.default_avatar)
                        .error(R.mipmap.default_avatar)
                        .into(headImg);
//                Uri uri=Uri.parse(memberModel.getHeadimg());
//                headImg.setImageURI(uri);
                if (memberModel == null || memberModel.getUname().equals("")) {
                    System.out.print("无昵称");
                    headName.setText("设置昵称");
                } else {
                    System.out.print("无昵称：" + memberModel.getNickname());
                    headName.setText(memberModel.getNickname());
                }
            } else {
                Log.d("AboutMine","未登录");
                memberModel = null;
                headName.setText("游客");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        headView = (RelativeLayout) findViewById(R.id.about_me_head_layout);
        headView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugFlags.logD(TAG, "去个人中心!");
                if (tempDataManager.isLogin()) {
                    Intent intent = new Intent(getActivity(), UserInfo.class);
                    startActivityForResult(intent, UserInfo.FLAG_HEAD_REFRESH);
                } else {
                    //跳转到登陆
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), INTENT_FLAG_LOGIN_REQ);
                }
            }
        });
        headImg = (RoundedImageView) findViewById(R.id.iv_about_me_head_img);
        headName = (TextView) findViewById(R.id.tv_about_me_title_name);

        llOrderTab = (LinearLayout) findViewById(R.id.ll_to_order_scan);
        llOrderTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugFlags.logD(TAG, "去订单详情！");
                if (tempDataManager.isLogin()) {
                    Intent intent = new Intent(getActivity(), OrderMineScan.class);
                    intent.putExtra("select", 1);
                    startActivity(intent);
                } else {
                    //跳转到登陆
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), INTENT_FLAG_LOGIN_REQ);
                }
            }
        });

        scanTab = (FixedIndicatorView) findViewById(R.id.about_me_order_scan_tab);

        final String[] scanTabName = new String[]{getResources().getString(R.string.order_waiting_pay), getResources().getString(R.string.order_waiting_grab),
                getResources().getString(R.string.order_have_accept), getResources().getString(R.string.order_delivery),
                getResources().getString(R.string.order_have_done)};
        final int[] drawableTop = new int[]{R.mipmap.user_card, R.mipmap.user_waiting, R.mipmap.user_receive, R.mipmap.user_distribution, R.mipmap.user_finish};
        scanTab.setAdapter(getAdapter(scanTabName, drawableTop, -1));

        scanTab.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                if (tempDataManager.isLogin()) {
                    scanTab.setAdapter(getAdapter(scanTabName, drawableTop, select));
                    Intent intent = new Intent(getActivity(), OrderMineScan.class);
                    intent.putExtra("select", select);
                    startActivity(intent);
                } else {
                    //跳转到登陆
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), INTENT_FLAG_LOGIN_REQ);
                }
            }
        });

    }

    private Indicator.IndicatorAdapter getAdapter(final String[] scanTabName, final int[] drawableTop, final int currueNum) {
        return new Indicator.IndicatorAdapter() {
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
                if (position == currueNum) {
                    textView.setTextColor(getResources().getColor(R.color.menu_yellow_color));
                }
                return convertView;
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //重新获取用户信息
        initUserInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        initUserInfo();
    }
}
