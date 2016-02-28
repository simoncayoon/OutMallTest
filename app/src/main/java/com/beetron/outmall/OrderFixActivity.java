package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.customview.BadgeView;
import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.customview.ViewWithBadge;
import com.beetron.outmall.models.AddrInfoModel;
import com.beetron.outmall.models.OrderFixInfo;
import com.beetron.outmall.models.OrderInfoModel;
import com.beetron.outmall.models.OrderPostModel;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.models.ShopCartModel;
import com.beetron.outmall.utils.BooleanSerializer;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/22.
 * Time: 14:45.
 */
public class OrderFixActivity extends Activity {

    public static final String ORDER_DETAIL_EXTRA_KEY = "ORDER_DETAIL_EXTRA_KEY";
    private static final String TAG = OrderFixActivity.class.getSimpleName();
    private static final String FLAG_PAYMENT_ONLINE = "1";
    private static final String FLAG_PAYMENT_DELIVERY = "2";
    private static final String GENERATE_GOODS = "GENERATE_GOODS";//生成商品ID
    private static final String GENERATE_NUM = "GENERATE_NUM";//生成商品对应的数量
    private LinearLayout llToAddrMng;
    private TextView tvAddrTitle, addrAddrDetal;
    private TextView tvTotalPrice, tvServiceFee, tvFree, tvActuallyPay;
    private TextView tvBottomTotal;
    private EditText etLeaveMsg;
    private ScrollIndicatorView proScanner;
    private RadioGroup paymentGroup;
    private Button btnCommitOrder;
    private CusNaviView cusNaviView;

    private AddrInfoModel addrInfo;
    private OrderFixInfo orderFixInfo;
    private OrderInfoModel orderInfoModel;
    private OrderPostModel orderPostModel = new OrderPostModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_fix_layout);
        initView();
        try {
            getOrderCondition();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initData();
    }

    private void initData() {
        try {
            orderInfoModel = OrderInfoModel.getInstance();
            orderPostModel.setPayment(FLAG_PAYMENT_ONLINE);
            proScanner.setAdapter(new Indicator.IndicatorAdapter() {
                @Override
                public int getCount() {
                    return orderInfoModel.getProDetail().size();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolder viewHolder = null;
                    if (convertView == null) {
                        viewHolder = new ViewHolder();
                        convertView = getLayoutInflater().inflate(R.layout.order_pro_scan_item_layout, parent, false);
                        viewHolder.scanImg = (NetworkImageView) convertView.findViewById(R.id.niv_order_pro_scan_img);
                        viewHolder.proPrice = (TextView) convertView.findViewById(R.id.order_pro_scan_price);
                        viewHolder.countView = new BadgeView(OrderFixActivity.this, viewHolder.scanImg);
                        viewHolder.countView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                        convertView.setTag(R.id.order_scanner_id, viewHolder);
                    }
                    viewHolder = (ViewHolder) convertView.getTag(R.id.order_scanner_id);
                    ShopCartModel shopCartModel = orderInfoModel.getProDetail().get(position);
                    viewHolder.scanImg.setImageUrl(shopCartModel.getGs().getImg(), NetController.getInstance(getApplicationContext()).getImageLoader());
                    viewHolder.proPrice.setText("￥" + shopCartModel.getGs().getPrice2());
                    viewHolder.countView.setText(shopCartModel.getNum());
                    viewHolder.countView.show();
                    return convertView;
                }

                class ViewHolder {
                    NetworkImageView scanImg;
                    TextView proPrice;
                    BadgeView countView;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {

        initNavi();
        llToAddrMng = (LinearLayout) findViewById(R.id.ll_order_fix_to_address);

        llToAddrMng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderFixActivity.this, AddrManager.class);
                startActivityForResult(intent, AddrManager.ADDR_REQUEST_CODE);
            }
        });

        tvAddrTitle = (TextView) findViewById(R.id.addr_info_item_title);
        addrAddrDetal = (TextView) findViewById(R.id.addr_info_item_detail);
        proScanner = (ScrollIndicatorView) findViewById(R.id.siv_order_fix_pro_scan);
        proScanner.setSplitAuto(false);

        paymentGroup = (RadioGroup) findViewById(R.id.order_fix_payment_choice);
        paymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.order_fix_payment_online) {
                    orderPostModel.setPayment(FLAG_PAYMENT_ONLINE);
                    return;
                }
                if (checkedId == R.id.order_fix_payment_delivery) {
                    orderPostModel.setPayment(FLAG_PAYMENT_DELIVERY);
                    return;
                }
            }
        });

        tvTotalPrice = (TextView) findViewById(R.id.tv_order_product_amount);
        tvServiceFee = (TextView) findViewById(R.id.tv_order_service_fee);
        tvFree = (TextView) findViewById(R.id.tv_order_service_fee_minus);
        tvActuallyPay = (TextView) findViewById(R.id.tv_order_pay_actually);
        etLeaveMsg = (EditText) findViewById(R.id.order_fix_leave_message);
        tvBottomTotal = (TextView) findViewById(R.id.tv_order_fix_payment_total);

        btnCommitOrder = (Button) findViewById(R.id.btn_to_account_shop_cart);
        btnCommitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    toSettleAccounts();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initNavi() {
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        cusNaviView.setNaviTitle(getResources().getString(R.string.navi_title_order_fix));
        cusNaviView.setBtn(CusNaviView.PUT_BACK_ENABLE, CusNaviView.NAVI_WRAP_CONTENT, 56);
        ((Button) cusNaviView.getLeftBtn()).setText(getResources().getString(R.string.navi_title_shop_cart));//设置返回标题

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AddrManager.ADDR_REQUEST_CODE) {
                addrInfo = data.getParcelableExtra(AddrManager.ADDR_PICK);
                DebugFlags.logD(TAG, addrInfo.getMobile());
                updateAddrInfo();
            }
        }
    }

    private void updateAddrInfo() {
        if (TextUtils.isEmpty(addrInfo.getName())) {
            return;
        }
        String gender;
        if (addrInfo.getSex().equals("1")) {
            gender = "男";
        } else {
            gender = "女";
        }
        tvAddrTitle.setText("收货人：" + addrInfo.getName() + "(" + gender + ") | " + addrInfo.getMobile());
        addrAddrDetal.setText("收货地址：" + addrInfo.getAddress());
    }

    void getOrderCondition() throws Exception {
        String url = NetInterface.HOST + NetInterface.METHON_ORDER_FIX;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(Constants.POST_UID_TEST);
        postEntity.setIsLogin("1");
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
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
                        if (resultEntity.isSuccess()) {
                            try {
                                initBottonData(resultEntity.getResult());
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
        NetController.getInstance(this).addToRequestQueue(getCategoryReq, TAG);
    }

    private void initBottonData(OrderFixInfo orderFixInfo) throws JSONException {
        this.orderFixInfo = orderFixInfo;
        addrInfo = orderFixInfo.getAddress();
        updateAddrInfo();

        try {
            tvTotalPrice.setText("￥" + orderInfoModel.getAmount());
            tvServiceFee.setText("￥" + String.valueOf(orderInfoModel.getAmount()));
            //free为1则直接免服务费（fuwu），free为0，则订单超过man值免服务费。
            if (orderFixInfo.getFree() == 1) {
                tvFree.setText("-￥" + orderFixInfo.getFuwu());//直接免
                orderInfoModel.setPriceFree(orderFixInfo.getFuwu());
            } else if (orderFixInfo.getFree() == 0) {
                if (orderInfoModel.getAmount() > orderFixInfo.getMan()) {
                    tvFree.setText("-￥" + orderFixInfo.getFuwu());
                    orderInfoModel.setPriceFree(orderFixInfo.getFuwu());
                } else {
                    tvFree.setText("-￥" + "0.0");
                }
            }
            tvServiceFee.setText("￥" + orderFixInfo.getFuwu());
            tvActuallyPay.setText("￥" + orderInfoModel.getAmount());
            SpannableString spannableString = new SpannableString("实付款： ￥ " + orderInfoModel.getAmount());
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.home_page_general_red)),
                    7, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//设置颜色
            spannableString.setSpan(new AbsoluteSizeSpan(13, true), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//设置“￥”字体
//            spannableString.setSpan(new AbsoluteSizeSpan(22, true), 7, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//设置价格字
            tvBottomTotal.setText(spannableString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void toSettleAccounts() throws Exception {

        String url = NetInterface.HOST + NetInterface.METHON_ORDER_COMMIT;

        orderPostModel.setToken(Constants.TOKEN_VALUE);
        orderPostModel.setUid(Constants.POST_UID_TEST);
        orderPostModel.setIsLogin("1");
        orderPostModel.setAddress_id(addrInfo.getId());
        orderPostModel.setRemark(etLeaveMsg.getText().toString());
        orderPostModel.setZongjia(String.valueOf(orderInfoModel.getAmount()));
        orderPostModel.setFuwufei(String.valueOf(orderFixInfo.getFuwu()));
        orderPostModel.setJianmian(String.valueOf(orderFixInfo.getFuwu()));
        orderPostModel.setTotalprice(orderInfoModel.getAmount() + orderFixInfo.getFuwu());
        orderPostModel.setGoods(generateDot(GENERATE_GOODS));
        orderPostModel.setNum(generateDot(GENERATE_NUM));

        Gson gson = new Gson();
        String postString = gson.toJson(orderPostModel,
                new TypeToken<OrderPostModel>() {
                }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest orderCommit = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());

                        try {
                            if (jsonObject.getString("isSuccess").equals("1")) {
                                OrderPostModel resultOrder = new OrderPostModel();
                                Gson gson = new Gson();
                                resultOrder = gson.fromJson(jsonObject.getJSONObject("result").getString("order"),
                                        new TypeToken<OrderPostModel>() {
                                        }.getType());
                                Intent intent = new Intent(OrderFixActivity.this, OrderDetailActivity.class);
                                intent.putExtra(OrderDetailActivity.INTENT_KEY_ADDR_INFO, addrInfo);
                                intent.putExtra(OrderDetailActivity.INTENT_KEY_ORDER_DATA, resultOrder);
                                startActivity(intent);
                            } else {
                                Toast.makeText(OrderFixActivity.this, getResources().getString(R.string.prompt_order_commit_faild),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        NetController.getInstance(getApplicationContext()).addToRequestQueue(orderCommit, TAG);
    }

    /**
     * 生成逗号分隔的字符串
     *
     * @param flag
     * @return
     */
    String generateDot(String flag) {

        String generateStr = "";
        StringBuilder sb = new StringBuilder(generateStr);
        for (int index = 0; index < orderInfoModel.getProDetail().size(); index++) {
            ShopCartModel shopCartItem = orderInfoModel.getProDetail().get(index);
            if (flag.equals(GENERATE_GOODS)) {
                sb.append(shopCartItem.getId());
            } else if (flag.equals(GENERATE_NUM)) {
                sb.append(shopCartItem.getNum());
            }
            if (index == orderInfoModel.getProDetail().size() - 1) {
                break;
            }
            sb.append(",");
        }
        DebugFlags.logD(TAG, "生成的逗号字符串 ：" + sb.toString());
        return sb.toString();
    }
}
