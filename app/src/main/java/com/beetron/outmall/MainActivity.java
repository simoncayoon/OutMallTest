package com.beetron.outmall;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.beetron.outmall.ObserveUtil.CountReciver;
import com.beetron.outmall.adapter.ProSummaryAdapter;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.customview.CustomDialog;
import com.beetron.outmall.models.UserInfoModel;
import com.beetron.outmall.utils.DBHelper;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.DisplayMetrics;
import com.beetron.outmall.utils.ShopCartChangReceiver;
import com.beetron.outmall.utils.TempDataManager;
import com.beetron.outmall.utils.UpdateHelper;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.viewpager.SViewPager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ProSummaryAdapter.ShopCartCountListener, CountReciver.UpdateCount {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String ACTION_STR = "com.chulai.mai.datachange";
    private static final int RESULT_LOGIN = 0x256;
    public static final int RESULT_ADD_SHOPCART = 0x213;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private IndicatorViewPager mIndicatorViewPager;
    private SViewPager viewPager;
    private CusNaviView cusNaviView;
    private ShopCartChangReceiver receiver;
    private CountReciver countReciver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        checkUpdate();
        countReciver = new CountReciver(this);
        IntentFilter intentFilter = new IntentFilter(CountReciver.COUNT_CHANGE_NOTIFICATION_ACTION);
        registerReceiver(countReciver, intentFilter);
    }

    private void initView() {
        initNavi();
        initDrawable();
        initIndicator();

        UserInfoModel userInfoModel = DBHelper.getInstance(getApplicationContext()).getUserInfo();

    }

    private void initNavi() {
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        cusNaviView.setBtn(CusNaviView.PUT_LEFT, 28, 28);
        cusNaviView.setNaviTitle(getResources().getString(R.string.framework_navi_home_page));
        ((Button) cusNaviView.getLeftBtn()).setBackgroundResource(R.mipmap.nav_ic_menu);

        cusNaviView.setBtn(CusNaviView.PUT_RIGHT, 23, 23);
        cusNaviView.getRightBtn().setBackgroundResource(R.mipmap.nav_ic_delete);
        cusNaviView.getRightBtn().setVisibility(View.GONE);
        cusNaviView.setNaviBtnListener(new CusNaviView.NaviBtnListener() {
            @Override
            public void leftBtnListener() {
                drawer.openDrawer(GravityCompat.START);
            }

            @Override
            public void rightBtnListener() {

                try {
                    ShopCart shopCart = (ShopCart) mIndicatorViewPager.getAdapter().getPagerAdapter().
                            instantiateItem(mIndicatorViewPager.getViewPager(), 2);
                    shopCart.notifyDelete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 初始化底部导航栏
     */
    private void initIndicator() {
        viewPager = (SViewPager) findViewById(R.id.tabmain_viewPager);
        Indicator indicator = (Indicator) findViewById(R.id.tabmain_indicator);
        mIndicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        final MyAdapter indicAdapter = new MyAdapter(getSupportFragmentManager());
        mIndicatorViewPager.setAdapter(indicAdapter);
        // 禁止viewpager的滑动事件
        viewPager.setCanScroll(false);
        // 设置viewpager保留界面不重新加载的页面数量
        viewPager.setOffscreenPageLimit(4);
        mIndicatorViewPager.setCurrentItem(0, false);
        indicator.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                cusNaviView.setNaviTitle(indicAdapter.tabNames[select]);
                mIndicatorViewPager.setCurrentItem(select, false);

                if (select == 2) {//添加购物车删除按钮
                    cusNaviView.getRightBtn().setVisibility(View.VISIBLE);
                    checkIsLogin();
                } else {
                    cusNaviView.getRightBtn().setVisibility(View.GONE);
                }
            }
        });
    }

    void checkIsLogin() {
        if (!TempDataManager.getInstance(this).isLogin()) {
            final CustomDialog.Builder builder = new CustomDialog.Builder(this);
            builder.setTitle(R.string.prompt);
            builder.setMessage(R.string.shop_cart_login_prompt);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra(LoginActivity.FLAG_NAVI_ROOT, getResources().getString(R.string.framework_navi_shope_cart));
                    startActivityForResult(intent, RESULT_LOGIN);
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
        } else {
            //更新购物车菜单视图
            ShopCart shopCart = (ShopCart) mIndicatorViewPager.getAdapter().getPagerAdapter().
                    instantiateItem(mIndicatorViewPager.getViewPager(), 2);
            try {
                shopCart.reqShopcart();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置抽屉
     */
    private void initDrawable() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
        if (id == R.id.nav_camera) {
            // Handle the camera action
            intent.putExtra("title", "商城");
            intent.putExtra("url", "http://chulai-mai.com");
        } else if (id == R.id.nav_gallery) {
            intent.putExtra("title", "蚤市");
            intent.putExtra("url", "http://chulai-mai.com");
        } else if (id == R.id.nav_slideshow) {
            intent.putExtra("title", "社区");
            intent.putExtra("url", "http://chulai-mai.com");
        } else if (id == R.id.nav_manage) {
            showLoginOut(item);
            return true;
        }

        startActivity(intent);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void showLoginOut(final MenuItem item) {
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(R.string.prompt);
        builder.setMessage(R.string.login_out_confirm);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    clearLocalData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                drawer.closeDrawer(GravityCompat.START);
                item.setVisible(false);
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

    /**
     * 清楚本地缓存，更新UI
     *
     * @throws Exception
     */
    private void clearLocalData() throws Exception {
        TempDataManager.getInstance(getApplicationContext()).clearCurrentTemp();//清除sharepreference缓存数据

        DBHelper.getInstance(getApplicationContext()).clearShopCart();

        DBHelper.getInstance(getApplicationContext()).clearUserInfo();

        notifyCountUpdate();

        try {
            //更新首页菜单视图
            ShopCart shopCart = (ShopCart) mIndicatorViewPager.getAdapter().getPagerAdapter().
                    instantiateItem(mIndicatorViewPager.getViewPager(), 2);
            shopCart.clearList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void notifyCountUpdate() {
        DebugFlags.logD(TAG, "广播收到的信息");
        /**
         * 此时有三处更新
         * 1、更新购物车tab的视图
         * 2、更新首页菜单栏
         * 3、更新产品列表状态
         */

        try {
            tabCountUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        proListUpdate();

    }

    void tabCountUpdate() throws Exception {
        TextView countView = (TextView) (mIndicatorViewPager.getIndicatorView().
                getItemView(2).findViewById(R.id.tv_badge_view_top_right));//获取到购物车tabbar的视图
        int localCount = DBHelper.getInstance(MainActivity.this).getShopCartCount();
        if (localCount == 0) {
            countView.setVisibility(View.GONE);
        } else {
            countView.setVisibility(View.VISIBLE);
            countView.setText(String.valueOf(localCount));
        }
    }

    /**
     * 商品列表更新
     */
    void proListUpdate() {
        //更新首页菜单视图
        try {
            HomeFragment homeFragment = (HomeFragment) mIndicatorViewPager.getAdapter().getPagerAdapter().
                    instantiateItem(mIndicatorViewPager.getViewPager(), 0);
            homeFragment.updateMenuItem();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ShopLimit shopLimit = (ShopLimit) mIndicatorViewPager.getAdapter().getPagerAdapter().
                    instantiateItem(mIndicatorViewPager.getViewPager(), 1);
            shopLimit.updateMenuItem();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addShopCart(int positon, Boolean isLimit) throws Exception {

        if (TempDataManager.getInstance(getApplicationContext()).isLogin()) {
            if (isLimit) {

                ShopLimit shopLimit = (ShopLimit) mIndicatorViewPager.getAdapter().getPagerAdapter().
                        instantiateItem(mIndicatorViewPager.getViewPager(), 1);
                shopLimit.addShopCart(positon);

            } else {

                HomeFragment homeFragment = (HomeFragment) mIndicatorViewPager.getAdapter().getPagerAdapter().
                        instantiateItem(mIndicatorViewPager.getViewPager(), 0);
                homeFragment.addShopCart(positon);
            }

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(LoginActivity.FLAG_NAVI_ROOT, getResources().getString(R.string.framework_navi_home_page));
            startActivityForResult(intent, RESULT_ADD_SHOPCART);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (requestCode == RESULT_LOGIN) {
                DebugFlags.logD(TAG, "点击购物车的Fragment 返回");

                try {
                    ShopCart shopCart = (ShopCart) mIndicatorViewPager.getAdapter().getPagerAdapter().
                            instantiateItem(mIndicatorViewPager.getViewPager(), 2);

                    shopCart.reqShopcart();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            if (requestCode == RESULT_ADD_SHOPCART) {
                DebugFlags.logD(TAG, "点击添加商品购物车登陆返回");
            }


        }
    }

    private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        Resources rsc = getResources();
        public final String[] tabNames = {rsc.getString(R.string.framework_navi_home_page), rsc.getString(R.string.framework_navi_time_limit), rsc.getString(R.string.framework_navi_shope_cart),
                rsc.getString(R.string.framework_navi_mine)};
        private int[] tabIcons = new int[]{R.drawable.tab_home_page_icon_selector, R.drawable.tab_sub_icon_selector, R.drawable.tab_shope_cart_selector,
                R.drawable.tab_mine_seletor};
        private LayoutInflater inflater;

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            inflater = LayoutInflater.from(getApplicationContext());
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.badge_view_top_right, container, false);
            }
            TextView tvContent = (TextView) convertView.findViewById(R.id.tv_badge_view_content);
            TextView tvBadge = (TextView) convertView.findViewById(R.id.tv_badge_view_top_right);
            tvBadge.setVisibility(View.GONE);
            tvContent.setText(tabNames[position]);
            tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            Drawable top = getResources().getDrawable(tabIcons[position]);
            if (position < 2) {
                top.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(
                        MainActivity.this, 22), DisplayMetrics.dip2px(
                        MainActivity.this, 22)));

            } else if (position == 3) {
                top.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(
                        MainActivity.this, 22), DisplayMetrics.dip2px(
                        MainActivity.this, 22)));
            } else if (position == 2) {
                top.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(
                        MainActivity.this, 22), DisplayMetrics.dip2px(
                        MainActivity.this, 22)));
            } else {
                top.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(
                        MainActivity.this, 22), DisplayMetrics.dip2px(
                        MainActivity.this, 22)));
            }
            tvContent.setCompoundDrawables(null, top, null, null);
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            BaseFragment fragment;
            switch (position) {
                case 0:
                    fragment = new HomeFragment();
                    break;
                case 1:
                    fragment = new ShopLimit();
                    break;
                case 2:
                    fragment = new ShopCart();
                    try {

                        receiver = new ShopCartChangReceiver((ShopCartChangReceiver.ShopCartChange) fragment);
                        IntentFilter intentFilter = new IntentFilter(ACTION_STR);
                        registerReceiver(receiver, intentFilter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    fragment = new AboutMine();
                    break;
                default:
                    fragment = new BaseFragment();
                    break;
            }
            Bundle bundle = new Bundle();
            bundle.putString(BaseFragment.INTENT_STRING_TABNAME, tabNames[position]);
            bundle.putInt(BaseFragment.INTENT_INT_INDEX, position);
            fragment.setArguments(bundle);
            return fragment;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        try {
            unregisterReceiver(countReciver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IndicatorViewPager getIndicatorView() {
        return mIndicatorViewPager;
    }

    /**
     * 检查更新
     * .checkUrl("http://chulai-mai.com/index.php?m=home&c=app&a=update")
     */
    void checkUpdate() {
        UpdateHelper updateHelper = new UpdateHelper.Builder(MainActivity.this)
                .checkUrl(NetInterface.HOST + NetInterface.METHON_UPDATE_CHECK)
                .isAutoInstall(false) //设置为false需在下载完手动点击安装;默认值为true，下载后自动安装。
                .build();
        DebugFlags.logD(TAG, "完整的更新URL时 ：" + NetInterface.HOST + NetInterface.METHON_UPDATE_CHECK);
        updateHelper.check();
    }
}
