package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.TelCheckUtil;
import com.beetron.outmall.utils.TempDataManager;
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
    private CusNaviView cusNaviView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        initView();
    }

    private void initView() {

        initNavi();
        etPhoneNum = (EditText) findViewById(R.id.et_phone_input);
        etVerify = (EditText) findViewById(R.id.et_verify_code_input);
        etPwd = (EditText) findViewById(R.id.et_pwd_input);
        etInvite = (EditText) findViewById(R.id.et_invite_code_input);

        btnGetVerify = (Button) findViewById(R.id.btn_regist_get_verify_code);
        btnGetVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etPhoneNum.getText().toString())) {
                    Toast.makeText(RegisteActivity.this, getResources().getString(R.string.prompt_phone_num_empty), Toast.LENGTH_SHORT).show();
                    return ;
                } else if (!TelCheckUtil.isMobileNO(etPhoneNum.getText().toString())) {
                    Toast.makeText(RegisteActivity.this, getResources().getString(R.string.prompt_phone_num_not_match), Toast.LENGTH_SHORT).show();
                    return ;
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
                    if (!etVerify.getText().toString().equals(verifyCode)) {
                        Toast.makeText(RegisteActivity.this,
                                getResources().getString(R.string.prompt_regist_phone_verify_error), Toast.LENGTH_SHORT).show();
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

    private void initNavi() {
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        cusNaviView.setNaviTitle(getResources().getString(R.string.navi_title_user_regist));
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
     * 注册
     */
    private void regist() throws Exception {

        final ProgressHUD mProgressHUD;
        mProgressHUD = ProgressHUD.show(this, getResources().getString(R.string.prompt_progress_regist), true, false,
                null);
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

                        try {
                            if (jsonObject.getString(Constants.RESULT_STATUS_FIELD).equals(Constants.RESULT_SUCCEED_STATUS)) {//返回成功
                                JSONObject resultUid = jsonObject.getJSONObject(Constants.RESULT_CONTENT_FIELD);
                                TempDataManager.getInstance(getApplicationContext()).setCurrentUid(resultUid.getString("uid"));//保存用户ID
                                finish();
                            } else {
                                Toast.makeText(RegisteActivity.this, jsonObject.getString(Constants.RESULT_ERROR_FIELD).toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
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
        NetController.getInstance(this).addToRequestQueue(getCategoryReq, TAG);
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(etPhoneNum.getText().toString())) {
            Toast.makeText(RegisteActivity.this, getResources().getString(R.string.prompt_phone_num_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!TelCheckUtil.isMobileNO(etPhoneNum.getText().toString())) {
            Toast.makeText(this, getResources().getString(R.string.prompt_phone_num_not_match), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(etVerify.getText().toString())) {
            Toast.makeText(RegisteActivity.this, getResources().getString(R.string.prompt_phone_verify_code_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(etPwd.getText().toString())) {
            Toast.makeText(RegisteActivity.this, getResources().getString(R.string.prompt_pwd_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getRegistCode() throws JSONException {

        final ProgressHUD mProgressHUD;
        mProgressHUD = ProgressHUD.show(this, getResources().getString(R.string.prompt_progress_regist), true, false,
                null);
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

                        try {
                            if (jsonObject.getString(Constants.RESULT_STATUS_FIELD).equals(Constants.RESULT_SUCCEED_STATUS)) {//返回成功
                                JSONObject resultCode = jsonObject.getJSONObject(Constants.RESULT_CONTENT_FIELD);
                                verifyCode = resultCode.getString("code");
                                btnGetVerify.setClickable(false);//关闭按钮点击
                                refreshSecond();
                            } else {
                                Toast.makeText(RegisteActivity.this, jsonObject.getString(Constants.RESULT_ERROR_FIELD).toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
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
        NetController.getInstance(this).addToRequestQueue(getCategoryReq, TAG);
    }

    private void refreshSecond() {
        final int MAX_TIMEOUT = 60;//设置超时时间为60
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                for (int max = MAX_TIMEOUT; max >=  0; max --) {
                    Message msg = new Message();
                    Bundle currTime = new Bundle();
                    currTime.putInt("current_time", max);
                    msg.setData(currTime);
                    handler.sendMessage(msg);//每秒发送
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        new Thread(runnable).start();
    }

    android.os.Handler handler = new Handler(){
        String btnString  = "获取验证码";
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int currTime = msg.getData().getInt("current_time");
            if (currTime > 0){
                btnGetVerify.setText(btnString + "(" + currTime +")");
                DebugFlags.logD(TAG, btnString + "(" + currTime +")");
            } else {
                btnGetVerify.setClickable(true);
                verifyCode = "";
                btnGetVerify.setText(btnString );
                Toast.makeText(RegisteActivity.this, getResources().getString(R.string.prompt_regist_reget_verify_code),
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

}
