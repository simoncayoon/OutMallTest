package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beetron.outmall.adapter.AddrAdapter;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.customview.ProgressHUD;
import com.beetron.outmall.models.AddrInfoModel;
import com.beetron.outmall.models.PageEntity;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.utils.BooleanSerializer;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.TempDataManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/22.
 * Time: 18:04.
 */
public class AddrManager extends Activity implements View.OnClickListener {

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
    private CusNaviView cusNaviView;

    private Button btnLeft, btnRight;
    private TextView titleView;
    private ImageView backImage;

    private void initHeader() {
        btnLeft = (Button) findViewById(R.id.btn_left);
        btnRight = (Button) findViewById(R.id.btn_right);
        titleView = (TextView) findViewById(R.id.tv_center);
        backImage = (ImageView) findViewById(R.id.iv_back);
        btnLeft.setText(getResources().getString(R.string.navi_title_order_fix));
        btnRight.setText(getResources().getString(R.string.navi_title_right_manage));
        titleView.setText(getResources().getString(R.string.navi_title_addr_select));
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        backImage.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_manage_layout);
        addrList = new ArrayList<AddrInfoModel>();
        initView();

    }

    private void initView() {

        initHeader();
        llAddrList = (ListView) findViewById(R.id.shop_cart_detail_list);

        llEmpty = (LinearLayout) findViewById(R.id.addr_manage_empty_layout);
        btnToadd = (Button) findViewById(R.id.btn_add_addr_info);
        btnToadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addrList.clear();
                addrAdapter.notifyDataSetChanged();
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
    }

    private void reqAddr() throws Exception {
        final ProgressHUD mProgressHUD;
        mProgressHUD = ProgressHUD.show(AddrManager.this, getResources().getString(R.string.prompt_progress_loading), true, false,
                null);
        String url = NetInterface.HOST + NetInterface.METHON_GET_ADDR_LIST;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(TempDataManager.getInstance(getApplicationContext()).getCurrentUid());
        postEntity.setIsLogin(TempDataManager.getInstance(getApplicationContext()).getLoginState());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            DebugFlags.logD(TAG, jsonObject.toString());
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            BooleanSerializer serializer = new BooleanSerializer();
                            gsonBuilder.registerTypeAdapter(Boolean.class, serializer);
                            gsonBuilder.registerTypeAdapter(boolean.class, serializer);
                            Gson gson = gsonBuilder.create();
                            ResultEntity<PageEntity<AddrInfoModel>> resultEntity = gson.fromJson(jsonObject.toString(),
                                    new TypeToken<ResultEntity<PageEntity<AddrInfoModel>>>() {
                                    }.getType());
                            if (resultEntity.isSuccess()) {
                                addrList = resultEntity.getResult().getList();
                                addrAdapter = new AddrAdapter(AddrManager.this, addrList);
                                initData();
                            } else {
                                try {
                                    DebugFlags.logD(TAG, resultEntity.getError());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        mProgressHUD.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                mProgressHUD.dismiss();
            }
        });
        NetController.getInstance(getApplicationContext()).addToRequestQueue(getCategoryReq, TAG);
    }

    private void initData() {
        addrAdapter.notifyDataSetChanged();
        if (addrList.size() > 0) {
            llAddrList.setAdapter(addrAdapter);
            if (llEmpty.isShown()) {
                llEmpty.setVisibility(View.GONE);
            }
        } else {
            llEmpty.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
            case R.id.tv_center:
                finish();
                break;
            case R.id.btn_right:
                //去地址管理
                startActivity(new Intent(AddrManager.this, AddrConsole.class));
                break;
            default:
                break;
        }
    }
}
