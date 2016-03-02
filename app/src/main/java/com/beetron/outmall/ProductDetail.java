package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.beetron.outmall.customview.ProgressHUD;
import com.beetron.outmall.customview.ViewWithBadge;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ProDetail;
import com.beetron.outmall.models.ProSummary;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.utils.DBHelper;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.TempDataManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/15.
 * Time: 12:47.
 */
public class ProductDetail extends Activity {

    public static final String KEY_PRODUCT_ID = "KEY_PRODUCT_ID";
    private static final String TAG = ProductDetail.class.getSimpleName();
    private final String HTML_ENCODING = "UTF-8";
    private final String HTML_MIME_TYPE = "text/html";
    private CusNaviView navigationView;
    private IndicatorViewPager bannerViewPager;
    private ScrollIndicatorView descTab;
    private ProDetail proDetail;
    private TextView tvTitle, tvSalePrice, tvPrimaryPrice, tvSalesVolume;
    private WebView tvProDesc;
    private CheckBox collectBox;
    private Button btnAddShopCart, btnBuyImmediately;
    private int inShopCart = 0;

    /**
     * 幻灯片适配
     */
    private IndicatorViewPager.IndicatorPagerAdapter imgAdapter = new IndicatorViewPager.IndicatorViewPagerAdapter() {

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.image_scan_tab_guide, container, false);
            }
            return convertView;
        }

        @Override
        public View getViewForPage(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.home_page_image_scan_content, container, false);
                convertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            try {
                ((NetworkImageView) convertView).setImageUrl(proDetail.getImg().get(position).getPic(), NetController.getInstance(ProductDetail.this).getImageLoader());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        @Override
        public int getCount() {
            try {
                return proDetail.getImg().size();
            } catch (Exception e) {
                return 0;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail_layout);
        try {
            inShopCart = DBHelper.getInstance(this).getShopCartCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initView();
        try {
            requestProDetail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        tvTitle.setText(proDetail.getTitle());
        tvSalePrice.setText("￥ " + proDetail.getPrice2());
        tvPrimaryPrice.setText("原价 ￥" + proDetail.getPrice1());
        tvSalesVolume.setText("已销" + proDetail.getXiaoliang() + " 笔");

        try {
            final String miaoshu = proDetail.getMiaoshu();
            tvProDesc.loadDataWithBaseURL("file://", miaoshu, HTML_MIME_TYPE, HTML_ENCODING, "about:blank");
        } catch (Exception e) {
            e.printStackTrace();
        }

        bannerViewPager.setAdapter(imgAdapter);
    }

    private void requestProDetail() throws Exception {
        final ProgressHUD mProgressHUD;
        mProgressHUD = ProgressHUD.show(ProductDetail.this, getResources().getString(R.string.prompt_progress_loading), true, false,
                null);
        String url = NetInterface.HOST + NetInterface.METHON_GET_PRO_BY_SID;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setGid(getIntent().getStringExtra(KEY_PRODUCT_ID));
        DebugFlags.logD(TAG, "获取的产品ID ：" + getIntent().getStringExtra(KEY_PRODUCT_ID));
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest proDetailReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, "获取的商品详情：" + jsonObject.toString());
                        Gson gson = new Gson();
                        ResultEntity resultEntity = gson.fromJson(jsonObject.toString(), new TypeToken<ResultEntity<ProDetail>>() {
                        }.getType());
                        proDetail = (ProDetail) resultEntity.getResult();
                        initData();
                        mProgressHUD.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                mProgressHUD.dismiss();
            }
        });
        NetController.getInstance(this).addToRequestQueue(proDetailReq, TAG + "getImageList");

    }

    private void initView() {
        initNavi();

        initBanner();

        tvTitle = (TextView) findViewById(R.id.pro_detail_title);
        tvSalePrice = (TextView) findViewById(R.id.pro_detail_sale_price);
        tvPrimaryPrice = (TextView) findViewById(R.id.pro_detail_primary_price);
        tvSalesVolume = (TextView) findViewById(R.id.pro_detail_sales_volume);
        tvProDesc = (WebView) findViewById(R.id.pro_detail_descript);

        descTab = (ScrollIndicatorView) findViewById(R.id.pro_detail_tab_indicator);
        descTab.setScrollBar(new ColorBar(this, R.color.home_page_general_red, 3));

        descTab.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                if (select == 0) {//商品信息
                    try {
                        final String miaoshu = proDetail.getMiaoshu();
                        tvProDesc.loadDataWithBaseURL("file://", miaoshu, HTML_MIME_TYPE, HTML_ENCODING, "about:blank");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (select == 1) {//图文详情
                    try {
                        final String tuwen = proDetail.getTuwen();
                        tvProDesc.loadDataWithBaseURL("file://", tuwen, HTML_MIME_TYPE, HTML_ENCODING, "about:blank");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        final String[] tabTitle = new String[]{getResources().getString(R.string.pro_detail_tab_pro_info),
                getResources().getString(R.string.pro_detail_tab_pro_info)};
        descTab.setAdapter(new Indicator.IndicatorAdapter() {
            @Override
            public int getCount() {
                return tabTitle.length;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.condition_filter_tab_layout, parent, false);
                }
                TextView textView = (TextView) convertView.findViewById(R.id.tab_text_view);
                textView.setText(tabTitle[position]);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                return convertView;
            }
        });

        btnAddShopCart = (Button) findViewById(R.id.btn_product_detail_add_shop_cart);
        btnAddShopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProSummary proSummary = new ProSummary();
                proSummary.setFid(getIntent().getStringExtra(KEY_PRODUCT_ID));
                proSummary.setImg("");
                proSummary.setJianshu(proDetail.getMiaoshu());
                proSummary.setPrice1(proDetail.getPrice1());
                proSummary.setPrice2(proDetail.getPrice2());
                proSummary.setSid(proDetail.getSid());
                proSummary.setTitle(proDetail.getTitle());
                proSummary.setXl(proSummary.getXl());
                try {
                    addShopCart(proSummary);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btnBuyImmediately = (Button) findViewById(R.id.btn_product_detail_pay_immediately);
        btnBuyImmediately.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //购买页面
            }
        });
    }

    private void initBanner() {
        Indicator indicator = (Indicator) findViewById(R.id.svp_scan_image_indicator);
        ViewPager viewPager = (ViewPager) findViewById(R.id.svp_scan_image_content);
        bannerViewPager = new IndicatorViewPager(indicator, viewPager);
    }

    private void initNavi() {
        navigationView = (CusNaviView) findViewById(R.id.general_navi_id);
        navigationView.setNaviTitle(getResources().getString(R.string.product_detail));
        navigationView.setBtn(CusNaviView.PUT_BACK_ENABLE, CusNaviView.NAVI_WRAP_CONTENT, 56);
        ((Button) navigationView.getLeftBtn()).setText(getResources().getString(R.string.framework_navi_home_page));//设置返回标题

        navigationView.setBtnView(CusNaviView.PUT_RIGHT, new ViewWithBadge(this), 23, 23);
        navigationView.getRightBtn().setBackgroundResource(R.mipmap.nav_ic_shopping);
        ((ViewWithBadge) navigationView.getRightBtn()).setBadge(BadgeView.POSITION_TOP_RIGHT, inShopCart
                , 6, 0);//初始化
        navigationView.setGravity(Gravity.CENTER);
        navigationView.setNaviBtnListener(new CusNaviView.NaviBtnListener() {
            @Override
            public void leftBtnListener() {
                finish();
            }

            @Override
            public void rightBtnListener() {
                //去购物车
                startActivity(new Intent(ProductDetail.this, ShopCartActivity.class));
            }
        });
    }

    /**
     * 加入购物车
     * @param proSummary
     * @throws Exception
     */
    void addShopCart(final ProSummary proSummary) throws Exception {
        String url = NetInterface.HOST + NetInterface.METHON_ADD_SHOPCART_BY_ID;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(TempDataManager.getInstance(getApplicationContext()).getCurrentUid());
        postEntity.setIsLogin(TempDataManager.getInstance(getApplicationContext()).getLoginState());
        postEntity.setGid(proSummary.getSid());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());
                        try {
                            if (jsonObject.getString(Constants.RESULT_STATUS_FIELD).equals(Constants.RESULT_SUCCEED_STATUS)){//返回成功
                                try {
                                    JSONObject countJSON = jsonObject.getJSONObject(Constants.RESULT_CONTENT_FIELD);
                                    inShopCart ++;
                                    DBHelper.getInstance(ProductDetail.this).addShopCart(proSummary);
                                    ((ViewWithBadge) navigationView.getRightBtn()).setBadge(BadgeView.POSITION_TOP_RIGHT, inShopCart
                                            , 6, 0);//初始化

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(ProductDetail.this, jsonObject.getString(Constants.RESULT_ERROR_FIELD).toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();

            }
        });
        NetController.getInstance(getApplicationContext()).addToRequestQueue(getCategoryReq, TAG);
    }

}
