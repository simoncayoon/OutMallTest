package com.beetron.outmall.wxapi;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.beetron.outmall.R;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.customview.CustomDialog;
import com.beetron.outmall.customview.ProgressHUD;
import com.beetron.outmall.models.AddrInfoModel;
import com.beetron.outmall.models.OrderFixInfo;
import com.beetron.outmall.models.OrderInfoModel;
import com.beetron.outmall.models.OrderPostModel;
import com.beetron.outmall.models.PayInfoModel;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ProSummary;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.utils.BooleanSerializer;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.ListViewUtil;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.PayHelper;
import com.beetron.outmall.utils.TempDataManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    public static final String INTENT_KEY_ORDER_MODEL = "INTENT_KEY_ORDER_MODEL";
    public static final String INTENT_KEY_ORDER_DATA = "INTENT_KEY_ORDER_DATA";
    public static final String INTENT_KEY_ADDR_INFO = "INTENT_KEY_ADDR_INFO";
    private static final String TAG = WXPayEntryActivity.class.getSimpleName();
    public static final String INTENT_KEY_BACK_TITLE_FLAG = "INTENT_KEY_FORM_FLAG";
    private static int MAX_ITEM_LINES = 3;

    private TextView orderNum;
    private TextView addrTitle, addrDetail;
    private TextView payType, proAmount;
    private TextView actualPay, orderDate, leaveMsg;
    private ListView lvProScan;
    private Button btnCancel, btnPayment;
    private LinearLayout llBtnZone;
    private OrderPostModel orderInfo;
    private AddrInfoModel addrInfo;
    private OrderInfoModel orderModel;
    private CusNaviView cusNaviView;
    private ProgressHUD mProgressHUD;
    private String orderId;
    private ImageView arrowRight;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail_layout);

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);

        initView();

        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
//        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(R.string.app_tip);
//            builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//            builder.show();
            switch (resp.errCode) {
//                0	成功	展示成功页面
//                -1	错误	可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
//                -2	用户取消	无需处理。发生场景：用户不支付了，点击取消，返回APP。
                case 0:
                    //Toast.makeText(this, "支付成功！", Toast.LENGTH_LONG).show();
                    upPayState(orderId);
                    break;
                case -1:
                    Toast.makeText(this, "发生错误！", Toast.LENGTH_LONG).show();
                    break;
                case -2:
                    Toast.makeText(this, "取消支付！", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }

    private void cancelOrder(final String msg) {
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(R.string.prompt);
        builder.setMessage(msg);
        builder.setCancelAble(false);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }

    private void initData() {
        orderInfo = getIntent().getParcelableExtra(INTENT_KEY_ORDER_DATA);
        addrInfo = getIntent().getParcelableExtra(INTENT_KEY_ADDR_INFO);
        orderModel = (OrderInfoModel) getIntent().getSerializableExtra(INTENT_KEY_ORDER_MODEL);

        orderNum.setText("订单号：" + orderInfo.getOrderno());
        setAddrInfo();

        payType.setText(orderInfo.getPayment().equals(Constants.PAYMENT_TYPE_ONLINE) ?
                getResources().getString(R.string.pay_online) : getResources().getString(R.string.pay_delivery));
        if (!orderInfo.getStatus().equals("1") ||
                orderInfo.getPayment().equals(Constants.PAYMENT_TYPE_DELIVERY)) {
            llBtnZone.setVisibility(View.GONE);
        }
        proAmount.setText("￥" + orderInfo.getZongjia());

        actualPay.setText("实付款 ￥" + orderInfo.getZongjia());
        orderDate.setText("下单时间：" + orderInfo.getDate());
        leaveMsg.setText(orderInfo.getRemark());

        lvProScan.setAdapter(new OrderProAdapter());
        ListViewUtil.setListViewHeightBasedOnChildrenT(lvProScan);
        btnCancel = (Button) findViewById(R.id.order_detail_order_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CustomDialog.Builder builder = new CustomDialog.Builder(WXPayEntryActivity.this);
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
//                Toast.makeText(WXPayEntryActivity.this, "正在开发中...", Toast.LENGTH_SHORT).show();
                try {
                    payOrder(orderInfo.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {

        initNavi();
        arrowRight = (ImageView) findViewById(R.id.addr_right_arrow);
        arrowRight.setVisibility(View.GONE);
        addrTitle = (TextView) findViewById(R.id.addr_info_item_title);
        addrDetail = (TextView) findViewById(R.id.addr_info_item_detail);
        orderNum = (TextView) findViewById(R.id.order_detail_order_num);

        payType = (TextView) findViewById(R.id.tv_order_payment_type);
        proAmount = (TextView) findViewById(R.id.tv_order_product_amount);
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
                                Toast.makeText(WXPayEntryActivity.this, getResources().getString(R.string.prompt_order_cancel),
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(WXPayEntryActivity.this, getResources().getString(R.string.prompt_order_cancel_failed),
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
                    NetController.getInstance(WXPayEntryActivity.this).getImageLoader());
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

    public String payOrder(final String orderID) {
        orderId = orderID;
        mProgressHUD = ProgressHUD.show(this, "正在处理...", true, false,
                null);
        PayHelper payHelper = new PayHelper(new PayHelper.PayListenner() {
            @Override
            public void payInfo(int status, PayInfoModel payInfoModel) {
                mProgressHUD.dismiss();
                if (status != 1) {
                    Toast.makeText(WXPayEntryActivity.this, "生成订单失败！！", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        final IWXAPI msgApi = WXAPIFactory.createWXAPI(WXPayEntryActivity.this, null);
                        // 将该app注册到微信
                        msgApi.registerApp(payInfoModel.getAppid());
                        IWXAPI api = WXAPIFactory.createWXAPI(WXPayEntryActivity.this, payInfoModel.getAppid());

                        boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
                        if (isPaySupported) {
                            PayReq req = new PayReq();
                            //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                            req.appId = payInfoModel.getAppid();
                            req.partnerId = payInfoModel.getPartnerid();
                            req.prepayId = payInfoModel.getPrepayid();
                            req.nonceStr = payInfoModel.getNoncestr();
                            req.timeStamp = payInfoModel.getTimestamp();
                            req.packageValue = payInfoModel.getPackageName();
                            req.sign = payInfoModel.getSign();
                            req.extData = orderID; // optional
                            Toast.makeText(WXPayEntryActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
                            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                            if (api.sendReq(req)) {
                                Toast.makeText(WXPayEntryActivity.this, "请求成功！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(WXPayEntryActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(WXPayEntryActivity.this, "该微信版本不支持微信支付！", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("PAY_GET", "异常：" + e.getMessage());
                        Toast.makeText(WXPayEntryActivity.this, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        try {
            payHelper.getPayInfo(WXPayEntryActivity.this, orderID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void upPayState(String orderId) {
        mProgressHUD = ProgressHUD.show(this, "订单处理中...", true, false,
                null);
        PayHelper payHelper = new PayHelper(new PayHelper.PayListenner() {
            @Override
            public void payInfo(int status, PayInfoModel payInfoModel) {
                mProgressHUD.dismiss();
                ;
                if (status == 1) {
                    cancelOrder("支付成功！");
                } else {
                    Toast.makeText(WXPayEntryActivity.this, "更新订单状态失败！请联系管理员！", Toast.LENGTH_LONG).show();
                }
            }
        });
        try {
            //更新订单状态
            payHelper.upPayInfo(WXPayEntryActivity.this, orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}