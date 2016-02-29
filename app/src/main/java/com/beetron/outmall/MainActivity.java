package com.beetron.outmall;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.beetron.outmall.adapter.ProSummaryAdapter;
import com.beetron.outmall.customview.BadgeView;
import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.customview.CustomDialog;
import com.beetron.outmall.customview.ViewWithBadge;
import com.beetron.outmall.models.ProSummary;
import com.beetron.outmall.utils.DBHelper;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.DisplayMetrics;
import com.beetron.outmall.utils.TempDataManager;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.viewpager.SViewPager;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ProSummaryAdapter.ShopCartCountListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    DrawerLayout drawer;
    private IndicatorViewPager mIndicatorViewPager;
    private SViewPager viewPager;
    private CusNaviView cusNaviView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {
        initNavi();
        initDrawable();
        initIndicator();
    }

    private void initNavi() {
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        cusNaviView.setBtn(CusNaviView.PUT_LEFT, 28, 28);
        cusNaviView.setNaviTitle("首页");
        ((Button) cusNaviView.getLeftBtn()).setBackgroundResource(R.mipmap.nav_ic_menu);
        cusNaviView.setNaviBtnListener(new CusNaviView.NaviBtnListener() {
            @Override
            public void leftBtnListener() {
                drawer.openDrawer(GravityCompat.START);
            }

            @Override
            public void rightBtnListener() {
                ShopCart shopCart = (ShopCart) mIndicatorViewPager.getAdapter().getPagerAdapter().
                        instantiateItem(mIndicatorViewPager.getViewPager(), 2);
                try {
                    shopCart.deleteShopCart();
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
                    cusNaviView.setBtn(CusNaviView.PUT_RIGHT, 23, 23);
                    cusNaviView.getRightBtn().setBackgroundResource(R.mipmap.nav_ic_delete);
                    checkIsLogin();
                } else {
                    cusNaviView.removeBtn(CusNaviView.PUT_RIGHT);
                }
            }
        });
    }

    void checkIsLogin(){
        if (!TempDataManager.getInstance(this).isLogin()){
            final CustomDialog.Builder builder = new CustomDialog.Builder(this);
            builder.setTitle(R.string.prompt);
            builder.setMessage(R.string.shop_cart_login_prompt);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra(LoginActivity.FLAG_NAVI_ROOT, getResources().getString(R.string.framework_navi_shope_cart));
                    startActivity(intent);
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
    }


    /**
     * 设置抽屉
     */
    private void initDrawable() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void notifyCountChange() throws Exception{

        DebugFlags.logD(TAG, "触发了数据更新！");
        try {
            ViewWithBadge shapCartTabView = (ViewWithBadge) (mIndicatorViewPager.getIndicatorView().
                    getItemView(2).findViewById(R.id.tab_text_view));//获取到购物车的视图
            shapCartTabView.setBadge(BadgeView.POSITION_TOP_RIGHT,
                    DBHelper.getInstance(MainActivity.this).getShopCartCount(), 6, 0);

            HomeFragment firstFragment = (HomeFragment) mIndicatorViewPager.getAdapter().getPagerAdapter().
                    instantiateItem(mIndicatorViewPager.getViewPager(), 0);

            List<ProSummary> shopCart = DBHelper.getInstance(getApplicationContext()).getShopCartList();
            for (ProSummary item : shopCart){
                firstFragment.updateMenuItem(item.getFid());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode){
            try {
                ShopCart shopCart = (ShopCart) mIndicatorViewPager.getAdapter().getPagerAdapter().
                        instantiateItem(mIndicatorViewPager.getViewPager(), 2);

                shopCart.reqShopcart();
            } catch (Exception e) {
                e.printStackTrace();
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
                convertView = inflater.inflate(R.layout.tab_main, container, false);
            }
            ViewWithBadge textView = (ViewWithBadge) convertView.findViewById(R.id.tab_text_view);
            textView.setText(tabNames[position]);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
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
            textView.setCompoundDrawables(null, top, null, null);
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
}
