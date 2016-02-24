package com.beetron.outmall;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beetron.outmall.adapter.ShopCartAdapter;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.models.ShopCartModel;
import com.beetron.outmall.models.ShopCartResult;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/3.
 * Time: 15:32.
 */
public class ShopCart extends BaseFragment {

    private static final String TAG = ShopCart.class.getSimpleName();
    private ListView lvShopcart;
    private CheckBox checkSelectAll;
    private TextView tvAmount;
    private Button btnAccount;
    private List<ShopCartModel> dataShopCart;
    private ShopCartResult shopcartResult;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.shop_cart_layout);

        initView();
        try {
            reqShopcart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {

        lvShopcart.setAdapter(new ShopCartAdapter(getActivity(), dataShopCart));

    }

    private void reqShopcart() throws Exception{
        String url = NetInterface.HOST + NetInterface.METHON_GET_SHOPCART;
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
                        Gson gson = new Gson();
                        ResultEntity<ShopCartResult> resultEntity = gson.fromJson(jsonObject.toString(),
                                new TypeToken<ResultEntity<ShopCartResult>>(){}.getType());
                        shopcartResult = resultEntity.getResult();
                        dataShopCart = shopcartResult.getList();
                        initData();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();

            }
        });
        NetController.getInstance(getApplicationContext()).addToRequestQueue(getCategoryReq, TAG);
    }

    private void initView() {
        lvShopcart = (ListView) findViewById(R.id.shop_cart_detail_list);
        checkSelectAll = (CheckBox) findViewById(R.id.cb_shop_cart_select_all);
        tvAmount = (TextView) findViewById(R.id.tv_shop_cart_amount);
        btnAccount = (Button) findViewById(R.id.btn_to_account_shop_cart);

        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), OrderFixActivity.class));
            }
        });
    }
}
