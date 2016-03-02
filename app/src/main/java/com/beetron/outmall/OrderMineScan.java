package com.beetron.outmall;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.beetron.outmall.customview.TabItemView;
import com.beetron.outmall.models.OrderInfo;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.TempDataManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/27.
 * Time: 22:38.
 */
public class OrderMineScan extends Activity implements View.OnClickListener {

    private TabItemView ti_title1, ti_title2, ti_title3, ti_title4, ti_title5;
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

    private CusNaviView cusNaviView;

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
        initNavi();
        initView();
        getData();
    }

    private void initView() {
        ti_title1 = (TabItemView) findViewById(R.id.ti_title1);
        ti_title2 = (TabItemView) findViewById(R.id.ti_title2);
        ti_title3 = (TabItemView) findViewById(R.id.ti_title3);
        ti_title4 = (TabItemView) findViewById(R.id.ti_title4);
        ti_title5 = (TabItemView) findViewById(R.id.ti_title5);
        setTextColor();
        upColor(select);
        ti_title5.hiddenLine();
        mListView = (ListView) findViewById(R.id.lv_orderList);
        mListView.setAdapter(mAdapter);
//        indicatorViewPager.setAdapter(new );
        ti_title1.setOnClickListener(this);
        ti_title2.setOnClickListener(this);
        ti_title3.setOnClickListener(this);
        ti_title4.setOnClickListener(this);
        ti_title5.setOnClickListener(this);
    }

    private void getData() {
        final ProgressHUD mProgressHUD;
        mProgressHUD = ProgressHUD.show(this, "正在加载...", true, false,
                null);

        String url = NetInterface.HOST + NetInterface.METHON_ORDER_INFO;
        OrderInfo postEntity = new OrderInfo();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setIsLogin(TempDataManager.getInstance(getApplicationContext()).getLoginState());
        postEntity.setUid(TempDataManager.getInstance(getApplicationContext()).getCurrentUid());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ti_title1:
                setTextColor();
                ti_title1.setText("已下单", R.color.menu_yellow_color);
                updateData(1);
                break;

            case R.id.ti_title2:
                setTextColor();
                ti_title2.setText("已支付", R.color.menu_yellow_color);
                updateData(2);
                break;

            case R.id.ti_title3:
                setTextColor();
                ti_title3.setText("已取消", R.color.menu_yellow_color);
                updateData(3);
                break;

            case R.id.ti_title4:
                setTextColor();
                ti_title4.setText("配送中", R.color.menu_yellow_color);
                updateData(4);
                break;

            case R.id.ti_title5:
                setTextColor();
                ti_title5.setText("已完成", R.color.menu_yellow_color);
                updateData(5);
                break;

            default:

                break;
        }
    }

    private void setTextColor() {
        ti_title1.setText("已下单", R.color.general_tab_text_color);
        ti_title2.setText("已支付", R.color.general_tab_text_color);
        ti_title3.setText("已取消", R.color.general_tab_text_color);
        ti_title4.setText("配送中", R.color.general_tab_text_color);
        ti_title5.setText("已完成", R.color.general_tab_text_color);
    }

    private void updateData(int type) {
        switch (type) {
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
    }


    private void upColor(int type) {
        switch (type) {
            case 1:
                ti_title1.setText("已下单", R.color.menu_yellow_color);
                break;

            case 2:
                ti_title2.setText("已支付", R.color.menu_yellow_color);
                break;

            case 3:
                ti_title3.setText("已取消", R.color.menu_yellow_color);
                break;

            case 4:
                ti_title4.setText("配送中", R.color.menu_yellow_color);
                break;

            case 5:
                ti_title5.setText("已完成", R.color.menu_yellow_color);
                break;

            default:

                break;
        }
    }
}
