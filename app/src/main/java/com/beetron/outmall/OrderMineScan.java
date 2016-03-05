package com.beetron.outmall;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beetron.outmall.adapter.OrderInfoAdapter;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.customview.ProgressHUD;
import com.beetron.outmall.customview.ViewWithBadge;
import com.beetron.outmall.models.OrderInfo;
import com.beetron.outmall.models.UserInfoModel;
import com.beetron.outmall.utils.DBHelper;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.DisplayMetrics;
import com.beetron.outmall.utils.NetController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.Indicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/27.
 * Time: 22:38.
 */
public class OrderMineScan extends Activity {

    //    private TabItemView ti_title1, ti_title2, ti_title3, ti_title4, ti_title5;
    private static final String TAG = OrderMineScan.class.getSimpleName();
    private ArrayList<JSONObject> orderList = new ArrayList<>();
    private ArrayList<JSONObject> payList = new ArrayList<>();
    private ArrayList<JSONObject> cannelList = new ArrayList<>();
    private ArrayList<JSONObject> transList = new ArrayList<>();
    private ArrayList<JSONObject> completeList = new ArrayList<>();
    private ListView mListView;
    private OrderInfoAdapter mAdapter;
    private int select = 1;
    private float density;
    private LinearLayout ll_shop_car;
    private FixedIndicatorView scanTab;
    UserInfoModel userInfoSummary;
    private CusNaviView cusNaviView;
    private ProgressHUD mProgressHUD;

    private void initNavi() {
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        cusNaviView.setBtn(CusNaviView.PUT_BACK_ENABLE, CusNaviView.NAVI_WRAP_CONTENT, 56);
        cusNaviView.setBtn(CusNaviView.PUT_RIGHT, CusNaviView.NAVI_WRAP_CONTENT, 56);
        cusNaviView.setNaviTitle("我消费的订单");

        ((Button) cusNaviView.getLeftBtn()).setText(getResources().getString(R.string.my));//设置返回标题

        cusNaviView.setNaviBtnListener(new CusNaviView.NaviBtnListener() {
            @Override
            public void leftBtnListener() {
                finish();
            }

            @Override
            public void rightBtnListener() {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_mine_scan_layout);
        select = getIntent().getExtras().getInt("select");
        mAdapter = new OrderInfoAdapter(this, orderList);
        userInfoSummary = DBHelper.getInstance(this).getUserInfo();
        initNavi();
        initView();
        getData();
    }

    private void initView() {
        ll_shop_car = (LinearLayout) findViewById(R.id.ll_shop_car);
        mListView = (ListView) findViewById(R.id.lv_orderList);
        mListView.setAdapter(mAdapter);
//        indicatorViewPager.setAdapter(new );

        scanTab = (FixedIndicatorView) findViewById(R.id.about_me_order_scan_tab);

        final String[] scanTabName = new String[]{getResources().getString(R.string.order_waiting_pay), getResources().getString(R.string.order_waiting_grab),
                getResources().getString(R.string.order_have_accept), getResources().getString(R.string.order_delivery),
                getResources().getString(R.string.order_have_done)};
        final int[] drawableTop = new int[]{R.mipmap.user_card, R.mipmap.user_waiting, R.mipmap.user_receive, R.mipmap.user_distribution, R.mipmap.user_finish};
        scanTab.setAdapter(getAdapter(scanTabName, drawableTop, select));

        scanTab.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                //设置菜单的点击事件
                scanTab.setAdapter(getAdapter(scanTabName, drawableTop, select));
                updateData(select);
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
                    convertView = getLayoutInflater().inflate(R.layout.tab_main, parent, false);
                }
                ViewWithBadge textView = (ViewWithBadge) convertView.findViewById(R.id.tab_text_view);
                textView.setText(scanTabName[position]);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                textView.setTextColor(getResources().getColor(R.color.general_tab_text_color));
                Drawable top = getResources().getDrawable(R.drawable.shape_line);
                top.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(
                        OrderMineScan.this, 1), DisplayMetrics.dip2px(
                        OrderMineScan.this, 20)));

                if (position == currueNum) {
                    textView.setTextColor(getResources().getColor(R.color.menu_yellow_color));
                }
                if (position != 4) {
                    textView.setCompoundDrawables(null, null, top, null);
                }
                textView.setCompoundDrawablePadding(50);
                return convertView;
            }
        };
    }

    private void getData() {
        mProgressHUD = ProgressHUD.show(this, "正在加载...", true, false,
                null);

        String url = NetInterface.HOST + NetInterface.METHON_ORDER_INFO;
        OrderInfo postEntity = new OrderInfo();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setIsLogin("1");
        postEntity.setUid(userInfoSummary.getUid());
        String postString = new Gson().toJson(postEntity, new TypeToken<OrderInfo>() {
        }.getType());
        JSONObject postJson = null;
        try {
            postJson = new JSONObject(postString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        mProgressHUD.hide();
                        DebugFlags.logD(TAG, jsonObject.toString());

                        Gson gson = new Gson();
                        try {
                            //Toast.makeText(OrderMineScan.this, "onResponse：" + jsonObject.toString(), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getString("isSuccess").equals(Constants.RESULT_SUCCEED_STATUS)) {
                                org.json.JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("list");
                                JSONObject object = null;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    object = jsonArray.getJSONObject(i);
                                    switch (object.getInt("status")) {
                                        case 1:
                                            orderList.add(object);
                                            break;
                                        case 2:
                                            payList.add(object);
                                            break;
                                        case 3:
                                            cannelList.add(object);
                                            break;
                                        case 4:
                                            transList.add(object);
                                            break;
                                        case 5:
                                            completeList.add(object);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                updateData(select);//更新数据
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mProgressHUD.hide();
                Toast.makeText(OrderMineScan.this, "请求失败！", Toast.LENGTH_SHORT).show();
            }
        });
        NetController.getInstance(getApplicationContext()).addToRequestQueue(getCategoryReq, TAG);
    }

    private void updateData(int type) {
        switch (type + 1) {
            case 1:
                mAdapter.upDate(orderList);
                break;
            case 2:
                mAdapter.upDate(payList);
                break;
            case 3:
                mAdapter.upDate(cannelList);
                break;
            case 4:
                mAdapter.upDate(transList);
                break;
            case 5:
                mAdapter.upDate(completeList);
                break;
            default:
                break;
        }
        mAdapter.notifyDataSetChanged();
        if (mAdapter.getCount() > 0) {
            ll_shop_car.setVisibility(View.INVISIBLE);
        } else {
            ll_shop_car.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressHUD.hide();
    }
}
