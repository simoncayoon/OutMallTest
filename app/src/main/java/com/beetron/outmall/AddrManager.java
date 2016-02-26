package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beetron.outmall.adapter.AddrAdapter;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.models.AddrInfoModel;
import com.beetron.outmall.models.PageEntity;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.models.ShopCartResult;
import com.beetron.outmall.utils.BooleanSerializer;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/22.
 * Time: 18:04.
 */
public class AddrManager extends Activity {

    public static final int ADDR_REQUEST_CODE = 0x1122;
    public static final String RESULT_DATA = "RESULT_DATA";
    private static final String TAG = AddrManager.class.getSimpleName();
    public static String ADDR_PICK = "ADDR_PICK";
    List<AddrInfoModel> addrList;
    private ListView llAddrList;
    private Button btnToadd;
    private LinearLayout llEmpty;
    private boolean isEdit = false;
    private AddrAdapter addrAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_manage_layout);
        initView();

    }

    private void initView() {
        llAddrList = (ListView) findViewById(R.id.shop_cart_detail_list);
        llEmpty = (LinearLayout) findViewById(R.id.addr_manage_empty_layout);
        btnToadd = (Button) findViewById(R.id.btn_add_addr_info);
        btnToadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddrManager.this, AddrEdit.class));
            }
        });

        llAddrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isEdit) {//编辑

                } else {//选择
                    Intent intent = new Intent(AddrManager.this, OrderFixActivity.class);
                    intent.putExtra(ADDR_PICK, addrList.get(position));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        llAddrList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    addrDelete(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
    }

    private void addrDelete(final int position) throws Exception{
        String url = NetInterface.HOST + NetInterface.METHON_GET_ADDR_LIST;
        AddrInfoModel postEntity = new AddrInfoModel();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(Constants.POST_UID_TEST);
        postEntity.setIsLogin("1");
        postEntity.setId(addrList.get(position).getId());
        String postString = new Gson().toJson(postEntity, new TypeToken<AddrInfoModel>() {
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
                        gsonBuilder.registerTypeAdapter(boolean.class, serializer);
                        Gson gson = gsonBuilder.create();
                        ResultEntity<PageEntity<AddrInfoModel>> resultEntity = gson.fromJson(jsonObject.toString(),
                                new TypeToken<ResultEntity<PageEntity<AddrInfoModel>>>() {
                                }.getType());
                        if (resultEntity.isSuccess()){
                            addrList.remove(position);
                            addrAdapter.notifyDataSetChanged();
                            if (addrList.size() == 0) {
                                llAddrList.setVisibility(View.GONE);
                                llEmpty.setVisibility(View.VISIBLE);
                            }
                        } else {
                            try {
                                DebugFlags.logD(TAG, resultEntity.getError());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

    private void reqAddr() throws Exception {
        String url = NetInterface.HOST + NetInterface.METHON_GET_ADDR_LIST;
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
                        gsonBuilder.registerTypeAdapter(boolean.class, serializer);
                        Gson gson = gsonBuilder.create();
                        ResultEntity<PageEntity<AddrInfoModel>> resultEntity = gson.fromJson(jsonObject.toString(),
                                new TypeToken<ResultEntity<PageEntity<AddrInfoModel>>>() {
                                }.getType());
                        if (resultEntity.isSuccess()){
                            addrList = resultEntity.getResult().getList();
                            initData();
                        } else {
                            try {
                                DebugFlags.logD(TAG, resultEntity.getError());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

    private void initData() {

        addrAdapter = new AddrAdapter(AddrManager.this, addrList);
        if (addrList.size() > 0) {
            llAddrList.setAdapter(addrAdapter);
        }else{
            llEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            reqAddr();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
