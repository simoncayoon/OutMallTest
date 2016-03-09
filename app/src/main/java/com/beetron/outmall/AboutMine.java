package com.beetron.outmall;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.customview.ProgressHUD;
import com.beetron.outmall.customview.ViewWithBadge;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.UserInfoModel;
import com.beetron.outmall.utils.DBHelper;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.DisplayMetrics;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.TempDataManager;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.Indicator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/3.
 * Time: 15:33.
 */
public class AboutMine extends BaseFragment implements View.OnClickListener {

    public static final int INTENT_FLAG_LOGIN_REQ = 0x11;
    private static final String TAG = AboutMine.class.getSimpleName();
    private RelativeLayout headView;
    private RoundedImageView headImg;
    private TextView headName, tvSign;
    private LinearLayout llOrderTab;
    private ImageView ImageSign;
    private FixedIndicatorView scanTab;
    private TempDataManager tempDataManager;
    private UserInfoModel memberModel;
    private ProgressHUD mProgressHUD;

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
                Log.d("AboutMine", "已登录");
                memberModel = DBHelper.getInstance(getActivity()).getUserInfo();
                Log.d("AboutMine", "用户头像：" + memberModel.getHeadimg());

                Glide.with(getActivity()).load(memberModel.getHeadimg()).placeholder(R.mipmap.default_avatar).into(headImg);
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
                Log.d("AboutMine", "未登录");
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
        ImageSign = (ImageView) findViewById(R.id.iv_sign);
        tvSign = (TextView) findViewById(R.id.tv_sign);
        ImageSign.setOnClickListener(this);
        tvSign.setOnClickListener(this);

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
                    Constants.SELECT_MENU=select;
                    scanTab.setAdapter(getAdapter(scanTabName, drawableTop, select));
                    Intent intent = new Intent(getActivity(), OrderMineScan.class);
                    //intent.putExtra("select", select);
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

    @Override
    public void onClick(View v) {

        //签到
        if (TempDataManager.getInstance(getApplicationContext()).isLogin()) {
            try {
                setSign();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //跳转到登陆
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), INTENT_FLAG_LOGIN_REQ);
        }
    }

    private void setSign() throws Exception {
        mProgressHUD = ProgressHUD.show(getActivity(), "正在处理...", true, false,
                null);

        String url = NetInterface.HOST + NetInterface.METHON_SIGN_INFO;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(memberModel.getUid());
        postEntity.setIsLogin("1");

        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (mProgressHUD.isShowing()) {
                            mProgressHUD.dismiss();
                        }
                        DebugFlags.logD(TAG, jsonObject.toString());
//                        Toast.makeText(UserInfo.this, "" + jsonObject.toString(), Toast.LENGTH_SHORT).show();
                        try {
                            if (jsonObject.getString("isSuccess").equals("1")) {
                                Toast.makeText(getActivity(), "签到成功！获得积分"+jsonObject.getJSONObject("result").getString("jifen"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(), "提交失败！", Toast.LENGTH_SHORT).show();
                if (mProgressHUD.isShowing()) {
                    mProgressHUD.dismiss();
                }
            }
        });
        NetController.getInstance(getApplicationContext()).addToRequestQueue(getCategoryReq, TAG);
    }

}
