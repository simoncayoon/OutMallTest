package com.beetron.outmall;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.beetron.outmall.customview.CustomDialog;
import com.beetron.outmall.customview.ProgressHUD;
import com.beetron.outmall.customview.ViewWithBadge;
import com.beetron.outmall.models.AddrInfoModel;
import com.beetron.outmall.models.OrderFixInfo;
import com.beetron.outmall.models.OrderInfo;
import com.beetron.outmall.models.OrderInfoModel;
import com.beetron.outmall.models.OrderPostModel;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ProSummary;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.models.UserInfoModel;
import com.beetron.outmall.utils.BooleanSerializer;
import com.beetron.outmall.utils.CannelOrderListenner;
import com.beetron.outmall.utils.DBHelper;
import com.beetron.outmall.utils.DebugFlags;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/27.
 * Time: 22:38.
 */
public class OrderMineScan extends Activity implements CannelOrderListenner {

    //    private TabItemView ti_title1, ti_title2, ti_title3, ti_title4, ti_title5;
    private static final String TAG = OrderMineScan.class.getSimpleName();
    private ArrayList<HashMap<String, Object>> orderList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> payList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> cannelList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> transList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> completeList = new ArrayList<>();
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
        mAdapter = new OrderInfoAdapter(this, orderList, select,this);
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
        Indicator.IndicatorAdapter adapter = getAdapter(scanTabName, select);
        scanTab.setAdapter(adapter);

        scanTab.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                //设置菜单的点击事件
                scanTab.setAdapter(getAdapter(scanTabName, select));
                updateData(select);
            }
        });
        //scanTab.setSplitMethod(10);

    }

    private Indicator.IndicatorAdapter getAdapter(final String[] scanTabName, final int currueNum) {
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
//                Drawable top = getResources().getDrawable(R.drawable.shape_line);
//                top.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(
//                        OrderMineScan.this, 1), DisplayMetrics.dip2px(
//                        OrderMineScan.this, 20)));
//
//                if (position != 4) {
//                    textView.setCompoundDrawables(null, null, top, null);
//                }
                textView.setCompoundDrawablePadding(1);
                if (position == currueNum) {
                    textView.setTextColor(getResources().getColor(R.color.menu_yellow_color));
                }
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
                        mProgressHUD.dismiss();
                        DebugFlags.logD(TAG, jsonObject.toString());

                        Gson gson = new Gson();
                        try {
                            //Toast.makeText(OrderMineScan.this, "onResponse：" + jsonObject.toString(), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getString("isSuccess").equals(Constants.RESULT_SUCCEED_STATUS)) {
                                orderList = new ArrayList<>();
                                payList = new ArrayList<>();
                                cannelList = new ArrayList<>();
                                transList = new ArrayList<>();
                                completeList = new ArrayList<>();
                                JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("list");
                                JSONObject json = null;
                                HashMap<String, Object> map = null;
                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    try {

                                    map = new HashMap<>();
                                    json = jsonArray.getJSONObject(i);

                                    Log.d("OrderInfoAdapter", "json:" + json.toString());
                                    OrderInfoModel orderInfoModel = OrderInfoModel.getInstance();
                                    OrderPostModel orderPostModel = new OrderPostModel();
                                    orderPostModel.setAddress_id(json.getString("address_id"));
                                    orderPostModel.setDate(json.getString("date"));
                                    orderPostModel.setFuwufei(json.getString("fuwufei"));
//            orderPostModel.setGoods(json.getString("date"));
                                    orderPostModel.setIsLogin("1");
                                    orderPostModel.setId(json.getString("id"));
                                    orderPostModel.setJianmian(json.getString("jianmian"));
                                    orderPostModel.setNum(json.getString("date"));
                                    orderPostModel.setOrderno(json.getString("orderno"));
                                    orderPostModel.setPayment(json.getString("payment"));
                                    orderPostModel.setRemark(json.getString("remark"));
                                    orderPostModel.setStatus(json.getString("status"));
                                    orderPostModel.setToken(Constants.TOKEN_VALUE);
                                    orderPostModel.setTotalprice(json.getDouble("totalprice"));
                                    orderPostModel.setZongjia(json.getString("zongjia"));
                                    orderPostModel.setUid(json.getString("uid"));


                                    JSONArray array = json.getJSONArray("gs");
                                    orderInfoModel.setAmount(json.getDouble("totalprice"));
                                    orderInfoModel.setPriceFree(json.getDouble("payment"));
                                    List<ProSummary> list = new ArrayList<>();
                                    ProSummary proSummary = null;
                                    JSONObject object = null;
                                    for (int j = 0; j < array.length(); j++) {
                                        object = array.getJSONObject(j);
                                        proSummary = new ProSummary();
                                        proSummary.setCount(object.getInt("num"));
                                        proSummary.setImg(object.getString("img"));
                                        proSummary.setTitle(object.getString("title"));
                                        proSummary.setPrice2(object.getDouble("price2"));
                                        list.add(proSummary);
                                    }
                                    orderInfoModel.setProDetail(list);
                                    map.put("orderInfoModels", orderInfoModel);
                                    map.put("orderPostModels", orderPostModel);

                                    switch (json.getInt("status")) {
                                        case 1:
                                            orderList.add(map);
                                            break;
                                        case 2:
                                            payList.add(map);
                                            break;
                                        case 3:
                                            cannelList.add(map);
                                            break;
                                        case 4:
                                            transList.add(map);
                                            break;
                                        case 5:
                                            completeList.add(map);
                                            break;
                                        default:
                                            break;
                                    }

//                                    } catch (Exception e) {
//                                    }
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
                mProgressHUD.dismiss();
                Toast.makeText(OrderMineScan.this, "请求失败！", Toast.LENGTH_SHORT).show();
            }
        });
        NetController.getInstance(getApplicationContext()).addToRequestQueue(getCategoryReq, TAG);
    }

    private void updateData(int type) {
        switch (type + 1) {
            case 1:
                mAdapter.upDate(orderList, select + 1);
                break;
            case 2:
                mAdapter.upDate(payList, select + 1);
                break;
            case 3:
                mAdapter.upDate(cannelList, select + 1);
                break;
            case 4:
                mAdapter.upDate(transList, select + 1);
                break;
            case 5:
                mAdapter.upDate(completeList, select + 1);
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
        if (mProgressHUD != null) {
            mProgressHUD.dismiss();
        }
    }

    @Override
    public void onCannel(String orderID) {
        cancelOrder(orderID);
    }

    @Override
    public void showDetail(OrderInfoModel orderInfoModel, OrderPostModel orderPostModel) {
        Intent intent=new Intent(this,OrderDetailActivity.class);
        AddrInfoModel addrInfoModel=new AddrInfoModel();
        addrInfoModel.setId(orderPostModel.getAddress_id());
        intent.putExtra(OrderDetailActivity.INTENT_KEY_ADDR_INFO,addrInfoModel);
        intent.putExtra(OrderDetailActivity.INTENT_KEY_ORDER_DATA,orderPostModel);
        startActivity(intent);
    }

    @Override
    public String payOrder() {
        return null;
    }


    private void cancelOrder(final String orderid) {
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(R.string.prompt);
        builder.setMessage("确定要取消此订单吗？");
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    orderCancel(orderid);//取消订单
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    void orderCancel(String orderid) throws Exception {
        final ProgressHUD mProgressHUD;
        mProgressHUD = ProgressHUD.show(OrderMineScan.this, getResources().getString(R.string.prompt_progress_loading), true, false,
                null);
        String url = NetInterface.HOST + NetInterface.METHON_ORDER_CANCEL;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setIsLogin(TempDataManager.getInstance(this).getLoginState());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        postJson.put("orderid", orderid);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD("OrderInfoAdapter", jsonObject.toString());
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        BooleanSerializer serializer = new BooleanSerializer();
                        gsonBuilder.registerTypeAdapter(Boolean.class, serializer);
                        Gson gson = gsonBuilder.create();
                        ResultEntity<OrderFixInfo> resultEntity = gson.fromJson(jsonObject.toString(),
                                new TypeToken<ResultEntity<OrderFixInfo>>() {
                                }.getType());
                        mProgressHUD.dismiss();
                        if (resultEntity.isSuccess()) {
                            try {
                                Toast.makeText(OrderMineScan.this, getResources().getString(R.string.prompt_order_cancel),
                                        Toast.LENGTH_SHORT).show();
                                getData();//重新加载数据
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(OrderMineScan.this, "取消订单失败！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                mProgressHUD.dismiss();
            }
        });
        NetController.getInstance(this).addToRequestQueue(getCategoryReq, TAG);
    }
}
