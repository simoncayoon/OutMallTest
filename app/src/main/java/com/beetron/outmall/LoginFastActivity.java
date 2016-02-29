package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
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
import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.customview.ProgressHUD;
import com.beetron.outmall.models.PostUser;
import com.beetron.outmall.models.ResultEntity;
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
 * Time: 17:00.
 */
public class LoginFastActivity extends Activity {

    private static final String TAG = LoginFastActivity.class.getSimpleName();
    private CusNaviView cusNaviView;
    private Button btnLoginFast, btnGetCode;
    private EditText etNum, etVeriCode;

    private String codeBindNum;//与验证码绑定的手机号
    private String verifyCode;//当前获取到的验证码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_fast_layout);

        initView();
    }

    private void initView() {
        initNavi();
        etNum = (EditText) findViewById(R.id.et_phone_input);
        etVeriCode = (EditText) findViewById(R.id.et_verify_code_input);

        btnGetCode = (Button) findViewById(R.id.btn_regist_get_verify_code);
        btnGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etNum.getText().toString())) {
                    Toast.makeText(LoginFastActivity.this, getResources().getString(R.string.prompt_phone_num_empty),
                            Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    etNum.setEnabled(false);
                    try {
                        getRegistCode();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnLoginFast = (Button) findViewById(R.id.btn_login_fast);
        btnLoginFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    try {
                        loginFast();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(etNum.getText().toString())) {
            Toast.makeText(this, getResources().getString(R.string.prompt_phone_num_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(etVeriCode.getText().toString())) {
            Toast.makeText(this, getResources().getString(R.string.prompt_phone_verify_code_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!etVeriCode.getText().toString().equals(verifyCode)){
            Toast.makeText(this, getResources().getString(R.string.prompt_verigy_code_error), Toast.LENGTH_SHORT).show();
            return false;
        }else if (!etNum.getText().toString().equals(codeBindNum)){
            Toast.makeText(this, getResources().getString(R.string.prompt_bind_code_num_error), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initNavi() {
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        cusNaviView.setNaviTitle(getResources().getString(R.string.navi_title_login_fast));
        cusNaviView.setBtn(CusNaviView.PUT_BACK_ENABLE, CusNaviView.NAVI_WRAP_CONTENT, 56);

        ((Button) cusNaviView.getLeftBtn()).setText(getResources().getString(R.string.navi_title_login));//设置返回标题

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

    /**
     * 登录
     */
    private void loginFast() throws Exception {

        final ProgressHUD mProgressHUD;
        mProgressHUD = ProgressHUD.show(this, getResources().getString(R.string.prompt_progress_login), true, false,
                null);
        String url = NetInterface.HOST + NetInterface.METHON_LOGIN_FAST;
        PostUser postEntity = new PostUser();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setMobile(etNum.getText().toString());
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
                                startActivity(new Intent(LoginFastActivity.this, MainActivity.class));
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

    /**
     * 获取验证码
     * @throws JSONException
     */
    private void getRegistCode() throws JSONException {
        final ProgressHUD mProgressHUD;
        mProgressHUD = ProgressHUD.show(this, getResources().getString(R.string.prompt_progress_login), true, false,
                null);
        String url = NetInterface.HOST + NetInterface.METHON_VERIFY_CODE_REGIST_GEN;
        PostUser postEntity = new PostUser();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setMobile(etNum.getText().toString());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostUser>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());
                        Gson gson = new Gson();
                        codeBindNum = etNum.getText().toString();
                        etNum.setEnabled(true);
                        try {
                            if (jsonObject.getString(Constants.RESULT_STATUS_FIELD).equals(Constants.RESULT_SUCCEED_STATUS)){
                                JSONObject jsonCode = jsonObject.getJSONObject(Constants.RESULT_CONTENT_FIELD);
                                verifyCode = jsonCode.getString("code");
                            } else {
                                Toast.makeText(LoginFastActivity.this, getResources().getString(R.string.prompt_verify_code_get_faild),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mProgressHUD.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                etNum.setEnabled(true);
                mProgressHUD.dismiss();
            }
        });
        NetController.getInstance(this).addToRequestQueue(getCategoryReq, TAG);
    }
}
