package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.PostUser;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/21.
 * Time: 14:38.
 */
public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText etPhoneNum, etPwd;
    private TextView toRegist, toLoginFast, toTakeBack;

    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        initView();
    }

    private void initView() {
        etPhoneNum = (EditText) findViewById(R.id.et_phone_input);
        etPwd = (EditText) findViewById(R.id.et_pwd_input);

        toRegist = (TextView) findViewById(R.id.btn_login_to_regist);
        toLoginFast = (TextView) findViewById(R.id.btn_login_to_fast_login);
        toTakeBack = (TextView) findViewById(R.id.btn_take_pwd_back);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {//登录
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    try {
                        login();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        toRegist.setOnClickListener(new View.OnClickListener() {//立即注册
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisteActivity.class));
            }
        });

        toLoginFast.setOnClickListener(new View.OnClickListener() {//手机快速登录
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, LoginFastActivity.class));
            }
        });

        toTakeBack.setOnClickListener(new View.OnClickListener() {//密码找回
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, PwdBackActivity.class));
            }
        });
    }

    private boolean checkInput() {
        if(TextUtils.isEmpty(etPhoneNum.getText().toString())){
            Toast.makeText(this, getResources().getString(R.string.prompt_phone_num_empty), Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(etPwd.getText().toString())){
            Toast.makeText(this, getResources().getString(R.string.prompt_pwd_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 登录
     */
    private void login() throws Exception{
        String url = NetInterface.HOST + NetInterface.METHON_VERIFY_CODE_REGIST;
        PostUser postEntity = new PostUser();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUname(etPhoneNum.getText().toString());
        postEntity.setUname(etPwd.getText().toString());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostUser>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        NetController.getInstance(this).addToRequestQueue(getCategoryReq, TAG);
    }


}
