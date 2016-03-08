package com.beetron.outmall;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/15.
 * Time: 12:47.
 */
public class ProductDetail extends FragmentActivity {

    public static final String KEY_PRODUCT_ID = "KEY_PRODUCT_ID";
    private static final String TAG = ProductDetail.class.getSimpleName();
    private CusNaviView navigationView;
    private IndicatorViewPager bannerViewPager;
    private ScrollIndicatorView descTab;
    private ProDetail proDetail;
    private TextView tvTitle, tvSalePrice, tvPrimaryPrice, tvSalesVolume;
    //    private WebView tvProDesc;
    private CheckBox collectBox;
    private Button btnAddShopCart, btnBuyImmediately;
    private int inShopCart = 0;
    private IndicatorViewPager proWebView;

    // 动画时间
    private int AnimationDuration = 1000;
    // 正在执行的动画数量
    private int number = 0;
    // 是否完成清理
    private boolean isClean = false;
    private FrameLayout animation_viewGroup;
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    // 用来清除动画后留下的垃圾
                    try {
                        animation_viewGroup.removeAllViews();
                    } catch (Exception e) {

                    }

                    isClean = false;

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail_layout);

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

        bannerViewPager.setAdapter(imgAdapter);


        final String[] tabTitle = new String[]{getResources().getString(R.string.pro_detail_tab_pro_info),
                getResources().getString(R.string.pro_detail_tab_tuwen_info)};

        proWebView.setAdapter(new IndicatorViewPager.IndicatorFragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return tabTitle.length;
            }

            @Override
            public View getViewForTab(int position, View convertView, ViewGroup container) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.condition_filter_tab_layout, container, false);
                }
                TextView textView = (TextView) convertView.findViewById(R.id.tab_text_view);
                textView.setText(tabTitle[position]);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                return convertView;
            }

            @Override
            public Fragment getFragmentForPage(int position) {
                BaseFragment fragment;
                fragment = new ProWebFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BaseFragment.INTENT_INT_INDEX, position);
                if (position == 0) {
                    bundle.putString(BaseFragment.INTENT_STRING_TABNAME, proDetail.getMiaoshu());
                } else if (position == 1) {
                    bundle.putString(BaseFragment.INTENT_STRING_TABNAME, proDetail.getTuwen());
                }
                fragment.setArguments(bundle);
                return fragment;
            }
        });
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
        animation_viewGroup = createAnimLayout();
        initNavi();

        initBanner();
        tvTitle = (TextView) findViewById(R.id.pro_detail_title);
        tvSalePrice = (TextView) findViewById(R.id.pro_detail_sale_price);
        tvPrimaryPrice = (TextView) findViewById(R.id.pro_detail_primary_price);
        tvSalesVolume = (TextView) findViewById(R.id.pro_detail_sales_volume);
//        tvProDesc = (WebView) findViewById(R.id.pro_detail_descript);

        descTab = (ScrollIndicatorView) findViewById(R.id.pro_detail_tab_indicator);
        descTab.setScrollBar(new ColorBar(this, R.color.home_page_general_red, 3));
        ViewPager viewPager = (ViewPager) findViewById(R.id.svp_pro_detal_web_content);
        proWebView = new IndicatorViewPager(descTab, viewPager);

        btnAddShopCart = (Button) findViewById(R.id.btn_product_detail_add_shop_cart);
        btnAddShopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //购买页面
                if (TempDataManager.getInstance(getApplicationContext()).isLogin()) {
                    ProSummary proSummary = new ProSummary();
                    proSummary.setFid(getIntent().getStringExtra(KEY_PRODUCT_ID));
                    try {
                        proSummary.setImg(proDetail.getImg().get(0).getPic());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                } else {
                    Intent intent = new Intent(ProductDetail.this, LoginActivity.class);
                    intent.putExtra(LoginActivity.FLAG_NAVI_ROOT, getResources().getString(R.string.framework_navi_shope_cart));
                    startActivity(intent);
                }
            }
        });
        btnBuyImmediately = (Button) findViewById(R.id.btn_product_detail_pay_immediately);
        btnBuyImmediately.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //购买页面
                if (TempDataManager.getInstance(getApplicationContext()).isLogin()) {
                    startActivity(new Intent(ProductDetail.this, ShopCartActivity.class));
                } else {
                    Intent intent = new Intent(ProductDetail.this, LoginActivity.class);
                    intent.putExtra(LoginActivity.FLAG_NAVI_ROOT, getResources().getString(R.string.framework_navi_shope_cart));
                    startActivity(intent);
                }
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

        navigationView.setGravity(Gravity.CENTER);
        navigationView.setNaviBtnListener(new CusNaviView.NaviBtnListener() {
            @Override
            public void leftBtnListener() {
                finish();
            }

            @Override
            public void rightBtnListener() {
                //去购物车
                if (TempDataManager.getInstance(getApplicationContext()).isLogin()) {
                    startActivity(new Intent(ProductDetail.this, ShopCartActivity.class));
                } else {
                    Intent intent = new Intent(ProductDetail.this, LoginActivity.class);
                    intent.putExtra(LoginActivity.FLAG_NAVI_ROOT, getResources().getString(R.string.framework_navi_shope_cart));
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 加入购物车
     *
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
                            if (jsonObject.getString(Constants.RESULT_STATUS_FIELD).equals(Constants.RESULT_SUCCEED_STATUS)) {//返回成功
                                try {
                                    JSONObject countJSON = jsonObject.getJSONObject(Constants.RESULT_CONTENT_FIELD);
                                    inShopCart++;
                                    DBHelper.getInstance(ProductDetail.this).addShopCart(proSummary);
                                    //((ViewWithBadge) navigationView.getRightBtn()).setBadge(BadgeView.POSITION_TOP_RIGHT, inShopCart, 6, 0);//初始化
                                    //加入购物车动画
                                    int[] start_location = new int[2];
                                    btnAddShopCart.getLocationInWindow(start_location);
                                    doAnim(getResources().getDrawable(R.mipmap.list_ic_shopping_red), start_location);

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

    private void doAnim(Drawable drawable, int[] start_location) {
        if (!isClean) {
            setAnim(drawable, start_location);
        } else {
            try {
                animation_viewGroup.removeAllViews();
                isClean = false;
                setAnim(drawable, start_location);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isClean = true;
            }
        }
    }

    /**
     * @param
     * @return void
     * @throws
     * @Description: 创建动画层
     */
    private FrameLayout createAnimLayout() {
        ViewGroup rootView = (ViewGroup) this.getWindow().getDecorView();
        FrameLayout animLayout = new FrameLayout(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;

    }

    /**
     * @param vg       动画运行的层 这里是frameLayout
     * @param view     要运行动画的View
     * @param location 动画的起始位置
     * @return
     * @deprecated 将要执行动画的view 添加到动画层
     */
    private View addViewToAnimLayout(ViewGroup vg, View view, int[] location) {
        int x = location[0];
        int y = location[1];
        vg.addView(view);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(dip2px(this,
                90), dip2px(this, 90));
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setPadding(5, 5, 5, 5);
        view.setLayoutParams(lp);

        return view;
    }

    /**
     * dip，dp转化成px 用来处理不同分辨路的屏幕
     *
     * @param context
     * @param dpValue
     * @return
     */
    private int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 动画效果设置
     *
     * @param drawable       将要加入购物车的商品
     * @param start_location 起始位置
     */
    private void setAnim(Drawable drawable, int[] start_location) {

        Animation mScaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF,
                0.1f);
        mScaleAnimation.setDuration(AnimationDuration);
        mScaleAnimation.setFillAfter(true);

        final ImageView iview = new ImageView(this);
        iview.setImageDrawable(drawable);
        final View view = addViewToAnimLayout(animation_viewGroup, iview,
                start_location);
        view.setAlpha(0.6f);

        int[] end_location = new int[2];
        navigationView.getRightBtn().getLocationInWindow(end_location);
        int endX = end_location[0];
        int endY = end_location[1] - start_location[1];

        Animation mTranslateAnimation = new TranslateAnimation(0, endX, 0, endY);
        Animation mRotateAnimation = new RotateAnimation(0, 180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateAnimation.setDuration(AnimationDuration);
        mTranslateAnimation.setDuration(AnimationDuration);
        AnimationSet mAnimationSet = new AnimationSet(true);

        mAnimationSet.setFillAfter(true);
        mAnimationSet.addAnimation(mRotateAnimation);
        mAnimationSet.addAnimation(mScaleAnimation);
        mAnimationSet.addAnimation(mTranslateAnimation);

        mAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                number++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub

                ((ViewWithBadge) navigationView.getRightBtn()).setBadge(BadgeView.POSITION_TOP_RIGHT, inShopCart
                        , 6, 0);//初始化
                number--;
                if (number == 0) {
                    isClean = true;
                    myHandler.sendEmptyMessage(0);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

        });
        view.startAnimation(mAnimationSet);

    }

    /**
     * 内存过低时及时处理动画产生的未处理冗余
     */
    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        isClean = true;
        try {
            animation_viewGroup.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isClean = false;
        super.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            inShopCart = DBHelper.getInstance(this).getShopCartCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((ViewWithBadge) navigationView.getRightBtn()).setBadge(BadgeView.POSITION_TOP_RIGHT, inShopCart
                , 6, 0);//初始化
    }

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
                convertView = getLayoutInflater().inflate(R.layout.pro_detail_top_banner_fitcenter, container, false);
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

}
