package com.beetron.outmall;

import android.content.Intent;
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
import com.beetron.outmall.adapter.ProSummaryAdapter;
import com.beetron.outmall.models.PageEntity;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ProCategory;
import com.beetron.outmall.models.ProSummary;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.adapter.CategoryMenuAdapter;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.DisplayMetrics;
import com.beetron.outmall.utils.NetController;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.viewpager.SViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/1/23.
 * Time: 11:32.
 */
public class HomeFragment extends BaseFragment {

    public static final int PAGE_SIZE = 10;//分页查询每页数量
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final String FLAG_FILTER_BY_SALES = "flag_filter_by_sales";
    private static final String FLAG_FILTER_BY_PRICE = "flag_filter_by_PRICE";

    private List<ProCategory> categories;
    private List<String> imgList;
    private List<String> fidCache;
    private String[] filterTitle;
    private String currentFid = "";

    private IndicatorViewPager imageScanner;
    private Indicator filterIndicator;
    private PullToRefreshListView llProList;
    private LayoutInflater inflater;

    private CategoryMenuAdapter menuAdapter;

    /**
     * 左侧菜单按钮
     */
    private ListView llProCategory;
    private boolean refresh = true;
    private int index = 1;
    private List<ProSummary> proList;
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
                ((NetworkImageView) convertView).setImageUrl(imgList.get(position), NetController.getInstance(getActivity()).getImageLoader());
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

    private void getImageScan() throws JSONException {
        String url = NetInterface.HOST + NetInterface.METHON_GET_IMG_SCAN;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getImageList = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, "图片浏览：" + jsonObject.toString());
                        imgList = new ArrayList<String>();
                        try {
                            if (jsonObject.getString("isSuccess").equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                for (int index = 0; index < jsonArray.length(); index++) {
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
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());
                        Gson gson = new Gson();
                        ResultEntity<List<ProCategory>> resultEntity = gson.fromJson(jsonObject.toString(),
                                new TypeToken<ResultEntity<List<ProCategory>>>() {
                                }.getType());
                        categories = resultEntity.getResult();
                        fidCache = new ArrayList<String>();
                        for (ProCategory item : categories) {
                            fidCache.add(item.getId());//保存当前分类ID列表，避免多次循环查找fid
                        }
                        try {
                            currentFid = categories.get(0).getId();//初始化当前的分类ID
                            refreshProByFid(currentFid);//初始化产品列表内容
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

                //根据不同分类ID， 刷新商品列表
                try {
                    refreshProByFid(currentFid);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int index = 0; index < categories.size(); index++) {//清除非选择项的状态
                    if (position == index) {
                        categories.get(position).setIsSelected(true);
                    } else {
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
//        SViewPager viewPager = (SViewPager) findViewById(R.id.home_filter_viewpager);
        filterIndicator = (Indicator) findViewById(R.id.home_filter_tab_indicator);
        llProList = (PullToRefreshListView) findViewById(R.id.product_pull_refresh_list);
        llProList.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(), ProductDetail.class));
            }
        });
        filterIndicator.setAdapter(new Indicator.IndicatorAdapter() {
            @Override
            public int getCount() {
                return filterTitle.length;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.condition_filter_tab_layout, parent, false);
                }
                TextView textView = (TextView) convertView.findViewById(R.id.tab_text_view);
                textView.setText(filterTitle[position]);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                Drawable right = getResources().getDrawable(R.mipmap.product_filter_down_arrow);//排序箭头
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
        });

        filterIndicator.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                DebugFlags.logD(TAG, "点击了项目" + select + "    xxx     " + preSelect);
                if (select == 1) {
                    if(selectItemView.getTag(R.id.step1) == null){
                        selectItemView.setTag(R.id.step1, true);
                    }else if((Boolean)selectItemView.getTag(R.id.step1)){
                        selectItemView.setTag(R.id.step2, true);
                        selectItemView.setTag(R.id.step1, false);

                    } else if((Boolean)selectItemView.getTag(R.id.step2)){
                        selectItemView.setTag(R.id.step1, true);
                        selectItemView.setTag(R.id.step2, false);
                    }
                } else if (select == 2) {
                    if(selectItemView.getTag(R.id.step1) == null){
                        selectItemView.setTag(R.id.step1, true);
                    }else if((Boolean)selectItemView.getTag(R.id.step1)){
                        selectItemView.setTag(R.id.step2, true);
                        selectItemView.setTag(R.id.step1, false);

                    } else if((Boolean)selectItemView.getTag(R.id.step2)){
                        selectItemView.setTag(R.id.step1, true);
                        selectItemView.setTag(R.id.step2, false);
                    }
                }
                try {
                    refreshProByFid(currentFid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initImageScanView() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.svp_scan_image_content);
        Indicator indicator = (Indicator) findViewById(R.id.svp_scan_image_indicator);
        imageScanner = new IndicatorViewPager(indicator, viewPager);
        inflater = LayoutInflater.from(getApplicationContext());
    }

    public void updateMenuItem(String fid) {
        DebugFlags.logD(TAG, "更新菜单！");
        ProCategory menuItem = categories.get(fidCache.indexOf(fid));
        menuItem.setCount(menuItem.getCount() + 1);
        menuAdapter.notifyDataSetChanged();
    }

    void refreshProByFid(String currentFid) throws Exception {
        String url = NetInterface.HOST + NetInterface.METHON_GET_PRO_BY_CATEGORY;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setP(index);
        postEntity.setL(PAGE_SIZE);
        try {
            postEntity.setFid(currentFid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        initData(jsonObject, "");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        NetController.getInstance(getActivity()).addToRequestQueue(getCategoryReq, TAG);
    }

    void initData(JSONObject responData, String flag) {
        DebugFlags.logD(TAG, responData.toString());
        Gson gson = new Gson();

        try {
            PageEntity pageEntity = gson.fromJson(responData.getString("result"),
                    new TypeToken<PageEntity<ProSummary>>() {
                    }.getType());
            List<ProSummary> dataRefresh = pageEntity.getList();

            try {
                if ((Boolean)filterIndicator.getItemView(filterIndicator.getCurrentItem()).getTag(R.id.step1)) {
                    Collections.sort(dataRefresh, new SortByFilter(checkFlag(), true));//根据条件排序
                } else if((Boolean)filterIndicator.getItemView(filterIndicator.getCurrentItem()).getTag(R.id.step2)){
                    Collections.sort(dataRefresh, new SortByFilter(checkFlag(), false));//根据条件排序
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (refresh) {
                proList = dataRefresh;
            } else {
                proList.addAll(dataRefresh);
            }
            updateMenuItem(currentFid);
            llProList.getRefreshableView().setAdapter(new ProSummaryAdapter(getActivity(), proList));
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前数据的排序方式，按条件分为销量和价格，按顺序和倒序方式排列
     *
     * @return 排序条件
     */
    private String checkFlag() {
        if (filterIndicator.getCurrentItem() == 1) {//按销量排序
            return FLAG_FILTER_BY_SALES;
        } else if (filterIndicator.getCurrentItem() == 2) {//按价格排序
            return FLAG_FILTER_BY_PRICE;
        } else {
            return "";
        }
    }

    class SortByFilter implements Comparator<ProSummary> {

        int flagDesc = 0;
        String flagFilter;
        boolean isDesc;

        /**
         * 根据条件按照指定排列方式 重新组装列表
         * @param flagFilter 排序条件
         * @param isDesc 排序方式
         */
        public SortByFilter(String flagFilter, boolean isDesc) {
            this.flagFilter = flagFilter;
            this.isDesc = isDesc;
        }

        public int compare(ProSummary arg0, ProSummary arg1) {

            //首先比较年龄，如果年龄相同，则比较名字
            int flagAsc = 0;
            if (flagFilter.equals(FLAG_FILTER_BY_SALES)) {
                flagAsc = arg0.getXl().compareTo(arg1.getXl());
            } else if (flagFilter.equals(FLAG_FILTER_BY_PRICE)) {
                flagAsc = arg0.getPrice2().compareTo(arg1.getPrice2());
            }

            //倒序
            if (flagAsc > 0) {
                flagDesc = -1;
            } else if (flagAsc < 0) {
                flagDesc = 1;
            }
            if (isDesc) {
                return flagDesc;
            } else {
                return flagAsc;
            }
        }
    }
}
