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
import com.beetron.outmall.models.PostUser;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/21.
 * Time: 10:33.
 */
public class RegisteActivity extends Activity {

    private static final String TAG = RegisteActivity.class.getSimpleName();
    private EditText etPhoneNum, etVerify, etPwd, etInvite;
    private Button btnGetVerify, btnRegist;

    private String registNum = "";
    private String verifyCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        initView();
    }

    private void initView() {
        etPhoneNum = (EditText) findViewById(R.id.et_phone_input);
        etVerify = (EditText) findViewById(R.id.et_verify_code_input);
        etPwd = (EditText) findViewById(R.id.et_pwd_input);
        etInvite = (EditText) findViewById(R.id.et_invite_code_input);

        btnGetVerify = (Button) findViewById(R.id.btn_regist_get_verify_code);
        btnGetVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etPhoneNum.getText().toString())) {
                    Toast.makeText(RegisteActivity.this,
                            getResources().getString(R.string.prompt_phone_num_empty), Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    getRegistCode();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        btnRegist = (Button) findViewById(R.id.btn_regist);
        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    if (!etPhoneNum.getText().toString().equals(registNum)) {
                        Toast.makeText(RegisteActivity.this,
                                getResources().getString(R.string.prompt_regist_phone_num_error), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        regist();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    /**
     * 注册
     */
    private void regist() throws Exception {
        String url = NetInterface.HOST + NetInterface.METHON_REGIST;
        PostUser postEntity = new PostUser();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setTel(etPhoneNum.getText().toString());
        postEntity.setUpass(etPwd.getText().toString());
        postEntity.setRefer(etInvite.getText().toString());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostUser>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());
                        Gson gson = new Gson();
                        ResultEntity<JSONObject> resultEntity = gson.fromJson(jsonObject.toString(),
                                new TypeToken<ResultEntity<JSONObject>>() {
                                }.getType());
                        if (resultEntity.isSuccess()) {//返回成功
                            DebugFlags.logD(TAG, "保存uid");
                        } else {
                            Toast.makeText(RegisteActivity.this, resultEntity.getError(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        NetController.getInstance(this).addToRequestQueue(getCategoryReq, TAG);
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(etPhoneNum.getText().toString())) {
            Toast.makeText(RegisteActivity.this, getResources().getString(R.string.prompt_phone_num_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(etVerify.getText().toString())) {
            Toast.makeText(RegisteActivity.this, getResources().getString(R.string.prompt_phone_verify_code_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(etPwd.getText().toString())) {
            Toast.makeText(RegisteActivity.this, getResources().getString(R.string.prompt_pwd_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getRegistCode() throws JSONException {
        String url = NetInterface.HOST + NetInterface.METHON_VERIFY_CODE_REGIST;
        PostUser postEntity = new PostUser();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setMobile(etPhoneNum.getText().toString());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostUser>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());
                        registNum = etPhoneNum.getText().toString();//设置当前验证的手机号码
                        Gson gson = new Gson();
                        ResultEntity<String> resultEntity = gson.fromJson(jsonObject.toString(),
                                new TypeToken<ResultEntity<String>>() {
                                }.getType());
                        if (resultEntity.isSuccess()) {//返回成功
                            try {
                                verifyCode = new JSONObject(resultEntity.getResult()).getString("code");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(RegisteActivity.this, resultEntity.getError(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        NetController.getInstance(this).addToRequestQueue(getCategoryReq, TAG);
    }

}
