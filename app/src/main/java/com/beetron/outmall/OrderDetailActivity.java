package com.beetron.outmall;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.customview.CustomDialog;
import com.beetron.outmall.customview.ProgressHUD;
import com.beetron.outmall.models.AddrInfoModel;
import com.beetron.outmall.models.OrderFixInfo;
import com.beetron.outmall.models.OrderInfoModel;
import com.beetron.outmall.models.OrderPostModel;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ProSummary;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.utils.BooleanSerializer;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.ListViewUtil;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.TempDataManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/26.
 * Time: 16:59.
 */
public class OrderDetailActivity extends Activity {

    public static final String INTENT_KEY_ORDER_DATA = "INTENT_KEY_ORDER_DATA";
    public static final String INTENT_KEY_ADDR_INFO = "INTENT_KEY_ADDR_INFO";
    private static final String TAG = OrderDetailActivity.class.getSimpleName();
    public static final String INTENT_KEY_BACK_TITLE_FLAG = "INTENT_KEY_FORM_FLAG";
    private static int MAX_ITEM_LINES = 3;

    private TextView orderNum;
    private TextView addrTitle, addrDetail;
    private TextView payType, proAmount;
    //    private TextView payType, proAmount, serviceFee, freeFee;
    private TextView actualPay, orderDate, leaveMsg;
    private ListView lvProScan;
    private Button btnCancel, btnPayment;
    private LinearLayout llBtnZone;

    private OrderPostModel orderInfo;
    private AddrInfoModel addrInfo;
    private OrderInfoModel orderModel;
    private CusNaviView cusNaviView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail_layout);

        initView();

        initData();
    }

    private void initData() {
        orderInfo = getIntent().getParcelableExtra(INTENT_KEY_ORDER_DATA);
        addrInfo = getIntent().getParcelableExtra(INTENT_KEY_ADDR_INFO);
        orderModel = OrderInfoModel.getInstance();

        orderNum.setText("订单号：" + orderInfo.getOrderno());
        setAddrInfo();

        payType.setText(orderInfo.getPayment().equals(Constants.PAYMENT_TYPE_ONLINE) ?
                getResources().getString(R.string.pay_online) : getResources().getString(R.string.pay_delivery));
        if (orderInfo.getPayment().equals(Constants.PAYMENT_TYPE_DELIVERY)) {
            llBtnZone.setVisibility(View.GONE);
        }
        proAmount.setText("￥" + orderInfo.getZongjia());
//        serviceFee.setText("￥" + orderInfo.getFuwufei());
//        freeFee.setText("-￥" + orderInfo.getJianmian());

        actualPay.setText("实付款 ￥" + orderInfo.getTotalprice());
        orderDate.setText("下单时间：" + orderInfo.getDate());
        leaveMsg.setText(orderInfo.getRemark());

        lvProScan.setAdapter(new OrderProAdapter());
//        ListViewUtil.setListViewHeightBasedOnChildren(lvProScan);
        ListViewUtil.setListViewHeightBasedOnChildrenT(lvProScan);
        btnCancel = (Button) findViewById(R.id.order_detail_order_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CustomDialog.Builder builder = new CustomDialog.Builder(OrderDetailActivity.this);
                builder.setTitle(R.string.prompt);
                builder.setMessage(R.string.prompt_order_delete_confirm);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            try {
                                orderCancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
        });
        btnPayment = (Button) findViewById(R.id.order_detail_order_pay);
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OrderDetailActivity.this, "正在开发中...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {

        initNavi();

        addrTitle = (TextView) findViewById(R.id.addr_info_item_title);
        addrDetail = (TextView) findViewById(R.id.addr_info_item_detail);
        orderNum = (TextView) findViewById(R.id.order_detail_order_num);

        payType = (TextView) findViewById(R.id.tv_order_payment_type);
        proAmount = (TextView) findViewById(R.id.tv_order_product_amount);
//        serviceFee = (TextView) findViewById(R.id.tv_order_service_fee);
//        freeFee = (TextView) findViewById(R.id.tv_order_service_fee_minus);
        actualPay = (TextView) findViewById(R.id.order_detail_pay_actually);
        orderDate = (TextView) findViewById(R.id.order_detail_order_date);
        leaveMsg = (TextView) findViewById(R.id.order_detail_leave_msg);

        lvProScan = (ListView) findViewById(R.id.order_detail_pro_scan);
        llBtnZone = (LinearLayout) findViewById(R.id.order_detail_btn_zone);
    }

    private void initNavi() {
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        cusNaviView.setNaviTitle(getResources().getString(R.string.navi_title_order_detail));
        cusNaviView.setBtn(CusNaviView.PUT_BACK_ENABLE, CusNaviView.NAVI_WRAP_CONTENT, 56);
        try {
            ((Button) cusNaviView.getLeftBtn()).setText(getIntent().getStringExtra(INTENT_KEY_BACK_TITLE_FLAG));//设置返回标题
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

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

    private void setAddrInfo() {
        if (TextUtils.isEmpty(addrInfo.getName())) {
            return;
        }
        String gender;
        if (addrInfo.getSex().equals("1")) {
            gender = "男";
        } else {
            gender = "女";
        }
        addrTitle.setText("收货人：" + addrInfo.getName() + "(" + gender + ") | " + addrInfo.getMobile());
        addrDetail.setText("收货地址：" + addrInfo.getAddress());
    }

    void orderCancel() throws Exception {
        final ProgressHUD mProgressHUD;
        mProgressHUD = ProgressHUD.show(this, getResources().getString(R.string.prompt_progress_loading), true, false,
                null);
        String url = NetInterface.HOST + NetInterface.METHON_ORDER_CANCEL;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setIsLogin(TempDataManager.getInstance(getApplicationContext()).getLoginState());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        postJson.put("orderid", orderInfo.getId());
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());
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
                                Toast.makeText(OrderDetailActivity.this, getResources().getString(R.string.prompt_order_cancel),
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(OrderDetailActivity.this, getResources().getString(R.string.prompt_order_cancel_failed),
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

    private class OrderProAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return orderModel.getProDetail().size();
        }

        @Override
        public Object getItem(int position) {
            return orderModel.getProDetail().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.order_detail_pro_scan_item_layout, null);
                viewHolder.proImg = (NetworkImageView) convertView.findViewById(R.id.order_detail_pro_img);
                viewHolder.proName = (TextView) convertView.findViewById(R.id.order_detail_pro_title);
                viewHolder.proPrice = (TextView) convertView.findViewById(R.id.order_detail_pro_item_price);
                viewHolder.proCount = (TextView) convertView.findViewById(R.id.order_detail_pro_item_count);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            ProSummary shopCartModel = orderModel.getProDetail().get(position);
            viewHolder.proImg.setImageUrl(shopCartModel.getImg(),
                    NetController.getInstance(OrderDetailActivity.this).getImageLoader());
            viewHolder.proName.setText(shopCartModel.getTitle());
            viewHolder.proPrice.setText("￥ " + shopCartModel.getPrice2());
            viewHolder.proCount.setText("x " + shopCartModel.getCount());
            return convertView;
        }

        class ViewHolder {
            NetworkImageView proImg;
            TextView proName, proPrice, proCount;
        }
    }
}
