package com.beetron.outmall;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ProCategory;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.adapter.CategoryMenuAdapter;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.DisplayMetrics;
import com.beetron.outmall.utils.NetController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.viewpager.SViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/1/23.
 * Time: 11:32.
 */
public class HomeFragment extends BaseFragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private List<ProCategory> categories ;
    private List<String> imgList;
    private List<String> fidCache;
    private String[]  filterTitle ;
    private String currentFid = "";

    private IndicatorViewPager imageScanner;
    private LayoutInflater inflater;

    private CategoryMenuAdapter menuAdapter;

    /**
     * 左侧菜单按钮
     */
    private ListView llProCategory;
    private IndicatorViewPager filterIndicator;
    /**
     * 幻灯片适配
     */
    private IndicatorViewPager.IndicatorPagerAdapter adapter = new IndicatorViewPager.IndicatorViewPagerAdapter() {

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.image_scan_tab_guide, container, false);
            }
            return convertView;
        }

        @Override
        public View getViewForPage(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.home_page_image_scan_content, container, false);
                convertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            try {
                ((NetworkImageView)convertView).setImageUrl(imgList.get(position), NetController.getInstance(getActivity()).getImageLoader());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return imgList.size();
        }
    };

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.home_page_layout);
        initView();
        initData();
    }

    /**
     * 获取数据
     */
    private void initData() {
        try {
            getMenu();
            getImageScan();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getImageScan() throws JSONException{
        String url = NetInterface.HOST + NetInterface.METHON_GET_IMG_SCAN;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>(){}.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getImageList = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, "图片浏览：" + jsonObject.toString());
                        imgList = new ArrayList<String>();
                        try {
                            if(jsonObject.getString("isSuccess").equals("1")){
                                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                                    for (int index = 0; index < jsonArray.length(); index ++){
                                        String picPath = jsonArray.getJSONObject(index).getString("pic");
                                        imgList.add(picPath);
                                    }
                                imageScanner.setAdapter(adapter);
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
        NetController.getInstance(getActivity()).addToRequestQueue(getImageList, TAG + "getImageList");
    }

    /**
     * 获取产品种类
     */
    private void getMenu() throws JSONException {
        String url = NetInterface.HOST + NetInterface.METHON_GET_PRO_CATEGORY;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>(){}.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());
                        Gson gson = new Gson();
                        ResultEntity<List<ProCategory>> resultEntity = gson.fromJson(jsonObject.toString(),
                                new TypeToken<ResultEntity<List<ProCategory>>>(){}.getType());
                        categories = resultEntity.getResult();
                        fidCache = new ArrayList<String>();
                        for (ProCategory item : categories) {
                            fidCache.add(item.getId());//保存当前分类ID列表，避免多次循环查找fid
                        }
                        try {
                            currentFid = categories.get(0).getId();//初始化当前的分类ID
                            categories.get(0).setIsSelected(true);//初始化菜单按钮效果
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        menuAdapter = new CategoryMenuAdapter(getActivity(), categories);
                        llProCategory.setAdapter(menuAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        NetController.getInstance(getActivity()).addToRequestQueue(getCategoryReq, TAG);
    }

    //初始化布局控件
    private void initView() {
        llProCategory = (ListView) findViewById(R.id.ll_left_menu_product_category);
        llProCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //改变状态
                ProCategory clickItem = categories.get(position);
                currentFid = clickItem.getId();

                for (int index = 0; index < categories.size(); index ++ ){
                    if(position == index){
                        categories.get(position).setIsSelected(true);
                    }else{
                        categories.get(index).setIsSelected(false);
                    }
                }
                DebugFlags.logD(TAG, "点击了：" + clickItem.getName() + "\n 当前分类ID：" + currentFid);
                menuAdapter.notifyDataSetChanged();
            }
        });
        initImageScanView();

        initFilterView();
    }

    private void initFilterView() {
        filterTitle = new String[]{"默认", "销量", "价格"};
//        filterTitle = new String[]{getResources().getString(R.string.home_page_filter_default,
//                getResources().getString(R.string.home_page_filter_sales),
//                getResources().getString(R.string.home_page_filter_price))};
        SViewPager viewPager = (SViewPager) findViewById(R.id.home_filter_viewpager);
        Indicator indicator = (Indicator) findViewById(R.id.home_filter_tab_indicator);
        filterIndicator = new IndicatorViewPager(indicator, viewPager);
        filterIndicator.setAdapter(new FilterAdapter(getActivity().getSupportFragmentManager()));
        // 禁止viewpager的滑动事件
        viewPager.setCanScroll(false);
        // 设置viewpager保留界面不重新加载的页面数量
        viewPager.setOffscreenPageLimit(3);

    }

    private void initImageScanView() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.svp_scan_image_content);
        Indicator indicator = (Indicator) findViewById(R.id.svp_scan_image_indicator);
        imageScanner = new IndicatorViewPager(indicator, viewPager);
        inflater = LayoutInflater.from(getApplicationContext());
    }

    public void updateMenuItem(String fid){
        DebugFlags.logD(TAG, "更新菜单！");
        ProCategory menuItem = categories.get(fidCache.indexOf(fid));
        menuItem.setCount(menuItem.getCount() + 1);
        menuAdapter.notifyDataSetChanged();
    }

    /**
     * 条件筛选数据适配
     */
    private class FilterAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {


        public FilterAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return filterTitle.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.condition_filter_tab_layout, container, false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.tab_text_view);
            textView.setText(filterTitle[position]);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            Drawable right = getResources().getDrawable(R.mipmap.product_filter_down_arrow);
            if (position == 1) {
                right.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(
                        getActivity(), 15), DisplayMetrics.dip2px(
                        getActivity(), 15)));
                textView.setCompoundDrawables(null, null, right, null);
            } else if (position == 2) {
                right.setBounds(new Rect(0, 0, DisplayMetrics.dip2px(
                        getActivity(), 15), DisplayMetrics.dip2px(
                        getActivity(), 15)));
                textView.setCompoundDrawables(null, null, right, null);
            }
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {

            LazyFragment productContent = new ProductContent();
            String flag = "";
            switch (position){
                case 0:
                    flag = ProductContent.FLAG_DEFAULT;
                    break;
                case 1:
                    flag = ProductContent.FLAG_SALE;
                    break;
                case 2:
                    flag = ProductContent.FLAG_PRICE;
                    break;
                default:
                    flag = "";
            }
            Bundle bundle = new Bundle();
            bundle.putString(ProductContent.BUNDLE_KEY, flag);
            productContent.setArguments(bundle);
            return productContent;
        }
    }
}
