package com.beetron.outmall;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.models.AddrInfoModel;
import com.beetron.outmall.models.PageEntity;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.utils.BooleanSerializer;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/23.
 * Time: 14:32.
 */
public class AddrEdit extends Activity{

    private static final String TAG = AddrEdit.class.getSimpleName();
    private EditText etName, etPhone, etGender, etAddr;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addr_edit_layout);

        initView();
    }

    private void initView() {
        etName = (EditText) findViewById(R.id.et_addr_edit_name);
        etPhone = (EditText) findViewById(R.id.et_addr_edit_phone_num);
        etGender = (EditText) findViewById(R.id.et_addr_edit_gender);
        etAddr = (EditText) findViewById(R.id.et_addr_edit_addr);

        btnSave = (Button) findViewById(R.id.test_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    try {
                        addAddr();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 检查输入情况
     * @return
     */
    private boolean checkInput() {
        if (TextUtils.isEmpty(etName.getText().toString())){
            Toast.makeText(AddrEdit.this, getResources().getString(R.string.prompt_addr_name_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etPhone.getText().toString())){
            Toast.makeText(AddrEdit.this, getResources().getString(R.string.prompt_addr_phone_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etAddr.getText().toString())){
            Toast.makeText(AddrEdit.this, getResources().getString(R.string.prompt_addr_addr_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void addAddr() throws Exception {
        String url = NetInterface.HOST + NetInterface.METHON_ADDR_ADD;
        AddrInfoModel postEntity = new AddrInfoModel();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(Constants.POST_UID_TEST);
        postEntity.setIsLogin("1");
        postEntity.setName(etName.getText().toString());
        postEntity.setSex("1");
        postEntity.setAddress(etAddr.getText().toString());
        postEntity.setMobile(etPhone.getText().toString());
        postEntity.setShenfen("1");
        postEntity.setShiqu("1");
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
                        ResultEntity<String> resultEntity = gson.fromJson(jsonObject.toString(),
                                new TypeToken<ResultEntity<String>>() {
                                }.getType());
                        if (resultEntity.isSuccess()){
                            Toast.makeText(getApplicationContext(), resultEntity.getResult(), Toast.LENGTH_SHORT).show();
                            finish();

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
}
