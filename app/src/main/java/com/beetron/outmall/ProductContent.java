package com.beetron.outmall;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beetron.outmall.models.PageEntity;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ProSummary;
import com.beetron.outmall.adapter.ProSummaryAdapter;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shizhefei.fragment.LazyFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/3.
 * Time: 15:40.
 */
public class ProductContent extends LazyFragment {

    public static final String FLAG_DEFAULT = "flag_default";
    public static final String FLAG_SALE = "flag_sale";
    public static final String FLAG_PRICE = "flag_price";
    public static final String BUNDLE_KEY = "bundle_key";
    private static final String TAG = ProductContent.class.getSimpleName();

    private static final int PAGE_SIZE = 10;//分页查询每页数量
    private static final int index = 1;

    private List<ProSummary> proList;

    private PullToRefreshListView lvContent;
    private boolean refresh = true;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setContentView(R.layout.product_list_content_layout);
        initPtrList();
        try {
            getProduct(index, PAGE_SIZE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getProduct(int index, int pageSize) throws JSONException{
        String url = NetInterface.HOST + NetInterface.METHON_GET_PRO_BY_CATEGORY;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setP(index);
        postEntity.setL(pageSize);
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>(){}.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());
                        Gson gson = new Gson();

                        try {
                            PageEntity pageEntity = gson.fromJson(jsonObject.getString("result"),
                                    new TypeToken<PageEntity<ProSummary>>(){}.getType());
                            List<ProSummary> dataRefresh = pageEntity.getList();
                            if (refresh) {
                                proList = dataRefresh;
                            } else {
                                proList.addAll(dataRefresh);
                            }
                            lvContent.getRefreshableView().setAdapter(new ProSummaryAdapter(getActivity(), proList));
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        NetController.getInstance(getActivity()).addToRequestQueue(getCategoryReq, TAG);
    }

    private void initPtrList() {
        lvContent = (PullToRefreshListView) findViewById(R.id.product_pull_refresh_list);
        lvContent.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(), ProductDetail.class));
            }
        });
    }
}
