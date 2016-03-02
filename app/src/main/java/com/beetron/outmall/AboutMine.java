package com.beetron.outmall;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.customview.ViewWithBadge;
import com.beetron.outmall.models.MemberModel;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.utils.BooleanSerializer;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.DisplayMetrics;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.TempDataManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.Indicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/3.
 * Time: 15:33.
 */
public class AboutMine extends BaseFragment {

    public static final int INTENT_FLAG_LOGIN_REQ = 0x11;
    private static final String TAG = AboutMine.class.getSimpleName();
    Button testRegist;
    private RelativeLayout headView;
    private NetworkImageView headImg;
    private TextView headName, headSign;
    private LinearLayout llOrderTab;
    private FixedIndicatorView scanTab;
    private TempDataManager tempDataManager;

    private MemberModel memberModel;

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
        tempDataManager = TempDataManager.getInstance(getActivity());
        initView();

        try {
            if (TempDataManager.getInstance(getApplicationContext()).isLogin()) {
                getUserData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUserData() throws Exception {
        String url = NetInterface.HOST + NetInterface.METHON_GET_USER_INFO;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(TempDataManager.getInstance(getApplicationContext()).getCurrentUid());
        postEntity.setIsLogin(TempDataManager.getInstance(getApplicationContext()).getLoginState());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());

                        try {
                            if (jsonObject.getString("isSuccess").equals("1")) {
                                JSONArray resultArray = (jsonObject.getJSONObject("result")).getJSONArray("user_info");
                                if (resultArray.length() > 0) {
                                    String resultUserInfo = resultArray.getString(0);//默认获取第一
                                    Gson gson = new Gson();
                                    memberModel = gson.fromJson(resultUserInfo, new TypeToken<MemberModel>() {
                                    }.getType());
                                    setUserInfo();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        GsonBuilder gsonBuilder = new GsonBuilder();
                        BooleanSerializer serializer = new BooleanSerializer();
                        gsonBuilder.registerTypeAdapter(Boolean.class, serializer);
                        Gson gson = gsonBuilder.create();

                        ResultEntity<JSONObject> resultEntity = gson.fromJson(jsonObject.toString(),
                                new TypeToken<ResultEntity<JSONObject>>() {
                                }.getType());
                        if (resultEntity.isSuccess()) {
                            try {
                                JSONObject userDetail = resultEntity.getResult().getJSONArray("user_info").getJSONObject(0);
                                memberModel = gson.fromJson(userDetail.toString(), new TypeToken<MemberModel>() {
                                }.getType());
                                setUserInfo();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        NetController.getInstance(getApplicationContext()).addToRequestQueue(getCategoryReq, TAG);
    }

    private void setUserInfo() {
        headImg.setImageUrl(memberModel.getHeadimg(), NetController.getInstance(getApplicationContext()).getImageLoader());
        if (memberModel == null) {
            headName.setText("昵称");
        } else {
            headName.setText(memberModel.getNickname());
        }
        headSign.setText(TempDataManager.getInstance(getActivity()).getUserSig());

    }

    private void initView() {
        headView = (RelativeLayout) findViewById(R.id.about_me_head_layout);
        headView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugFlags.logD(TAG, "去个人中心!");
                startActivity(new Intent(getActivity(), UserInfo.class));
            }
        });
        headImg = (NetworkImageView) findViewById(R.id.iv_about_me_head_img);
        headName = (TextView) findViewById(R.id.tv_about_me_title_name);
        headSign = (TextView) findViewById(R.id.tv_about_me_signature);

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
        scanTab.setAdapter(getAdapter(scanTabName, drawableTop, 0));

        scanTab.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                if (tempDataManager.isLogin()) {
                    scanTab.setAdapter(getAdapter(scanTabName, drawableTop, select));
                    Intent intent = new Intent(getActivity(), OrderMineScan.class);
                    intent.putExtra("select", select + 1);
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

        if (TempDataManager.getInstance(getApplicationContext()).isLogin()) {
            try {
                getUserData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
