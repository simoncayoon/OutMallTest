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
import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.customview.ProgressHUD;
import com.beetron.outmall.models.PostUser;
import com.beetron.outmall.models.UserInfoModel;
import com.beetron.outmall.utils.DBHelper;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.TempDataManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/21.
 * Time: 14:38.
 */
public class LoginActivity extends Activity {

    public static final String FLAG_NAVI_ROOT = "FLAG_NAVI_ROOT";
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText etPhoneNum, etPwd;
    private TextView toRegist, toLoginFast, toTakeBack;

    private Button btnLogin;
    private CusNaviView cusNaviView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        initView();
    }

    private void initView() {

        initNavi();
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

    private void initNavi() {
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        cusNaviView.setNaviTitle(getResources().getString(R.string.navi_title_login));
        cusNaviView.setBtn(CusNaviView.PUT_BACK_ENABLE, CusNaviView.NAVI_WRAP_CONTENT, 56);

        ((Button) cusNaviView.getLeftBtn()).setText(getIntent().getStringExtra(FLAG_NAVI_ROOT));//设置返回标题

        cusNaviView.setNaviBtnListener(new CusNaviView.NaviBtnListener() {
            @Override
            public void leftBtnListener() {
                finish();
            }

            @Override
            public void rightBtnListener() {

            }
        });
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(etPhoneNum.getText().toString())) {
            Toast.makeText(this, getResources().getString(R.string.prompt_phone_num_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(etPwd.getText().toString())) {
            Toast.makeText(this, getResources().getString(R.string.prompt_pwd_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 登录
     */
    private void login() throws Exception {

        final ProgressHUD mProgressHUD;
        mProgressHUD = ProgressHUD.show(this, getResources().getString(R.string.prompt_progress_login), true, false,
                null);
        String url = NetInterface.HOST + NetInterface.METHON_LOGIN;
        PostUser postEntity = new PostUser();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUname(etPhoneNum.getText().toString());
        postEntity.setUpass(etPwd.getText().toString());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostUser>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());

                        Gson gson = new Gson();
                        try {
                            if (jsonObject.getString(Constants.RESULT_STATUS_FIELD).equals(Constants.RESULT_SUCCEED_STATUS)) {
                                JSONObject jsonResult = jsonObject.getJSONObject(Constants.RESULT_CONTENT_FIELD);
                                UserInfoModel userInfoModel = new UserInfoModel();
                                userInfoModel = gson.fromJson(jsonResult.getString("user"),
                                        new TypeToken<UserInfoModel>() {
                                        }.getType());
                                DBHelper.getInstance(getApplicationContext()).saveUserInfo(userInfoModel);
                                TempDataManager.getInstance(getApplicationContext()).setCurrentUid(userInfoModel.getUid());
                                DebugFlags.logD(TAG, userInfoModel.getTel());
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mProgressHUD.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mProgressHUD.dismiss();
            }
        });
        NetController.getInstance(getApplicationContext()).addToRequestQueue(getCategoryReq, TAG);
    }
}
