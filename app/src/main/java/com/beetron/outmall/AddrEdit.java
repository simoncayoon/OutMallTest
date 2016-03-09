package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
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
import com.beetron.outmall.models.AddrInfoModel;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.utils.BooleanSerializer;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.TelCheckUtil;
import com.beetron.outmall.utils.TempDataManager;
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
public class AddrEdit extends Activity {

    private static final String TAG = AddrEdit.class.getSimpleName();
    public static String INTENT_KEY_ADDR_EDIT = "INTENT_KEY_ADDR_EDIT";
    private EditText etName, etPhone, etGender, etAddr;
    private CusNaviView cusNaviView;

    /**
     * 进入该界面模式
     */
    private Boolean isEdit = true;
    private AddrInfoModel postEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addr_edit_layout);
        postEntity = getIntent().getParcelableExtra(INTENT_KEY_ADDR_EDIT);
        if (postEntity == null) {//如果没有值，表示为新增地址
            postEntity = new AddrInfoModel();
            isEdit = false;
            initView();
        } else {
            try {
                initView();
                initData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void initData() throws Exception {
        etName.setText(postEntity.getName());
        etPhone.setText(postEntity.getMobile());
        etGender.setText(postEntity.getSex().equals("1") ? "男" : "女");
        etAddr.setText(postEntity.getAddress());
    }

    private void initView() {
        initNavi();
        etName = (EditText) findViewById(R.id.et_addr_edit_name);
        etPhone = (EditText) findViewById(R.id.et_addr_edit_phone_num);
        etGender = (EditText) findViewById(R.id.et_addr_edit_gender);
        etGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddrEdit.this, UserInfoFix.class);
                intent.putExtra(UserInfoFix.INTENT_KEY, UserInfoFix.INTENT_FLAG_GENDER_REQ);
                intent.putExtra(UserInfoFix.INTENT_VALUE, etGender.getText().toString());
                startActivityForResult(intent, UserInfoFix.INTENT_FLAG_GENDER_REQ);
            }
        });
        etAddr = (EditText) findViewById(R.id.et_addr_edit_addr);
    }

    private void initNavi() {
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        if (isEdit) {
            cusNaviView.setNaviTitle(getResources().getString(R.string.navi_title_addr_xiugai));
        } else {
            cusNaviView.setNaviTitle(getResources().getString(R.string.navi_title_addr_add));
        }
        cusNaviView.setBtn(CusNaviView.PUT_BACK_ENABLE, CusNaviView.NAVI_WRAP_CONTENT, 56);
        ((Button) cusNaviView.getLeftBtn()).setText(getResources().getString(R.string.navi_title_addr_manage));//设置返回标题

        cusNaviView.setBtnView(CusNaviView.PUT_RIGHT, new TextView(this), 50, 30);
        ((TextView) cusNaviView.getRightBtn()).setText(getResources().getString(R.string.complete));//初始化
        ((TextView) cusNaviView.getRightBtn()).setGravity(Gravity.CENTER);
        ((TextView) cusNaviView.getRightBtn()).setTextColor(getResources().getColor(R.color.general_main_title_color));
        ((TextView) cusNaviView.getRightBtn()).setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        cusNaviView.setNaviBtnListener(new CusNaviView.NaviBtnListener() {
            @Override
            public void leftBtnListener() {
                finish();
            }

            @Override
            public void rightBtnListener() {
                //提交修改记录
                if (checkInput()) {
                    try {
                        addAddr(isEdit);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 检查输入情况
     *
     * @return
     */
    private boolean checkInput() {

        DebugFlags.logD(TAG, "我的内容：" + etPhone.getText().toString());
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Toast.makeText(AddrEdit.this, getResources().getString(R.string.prompt_addr_name_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etPhone.getText().toString())) {
            Toast.makeText(AddrEdit.this, getResources().getString(R.string.prompt_addr_phone_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (!TelCheckUtil.isMobileNO(etPhone.getText().toString())){
            Toast.makeText(AddrEdit.this, getResources().getString(R.string.prompt_phone_num_not_match),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etAddr.getText().toString())) {
            Toast.makeText(AddrEdit.this, getResources().getString(R.string.prompt_addr_addr_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void addAddr(Boolean isEdit) throws Exception {

        final ProgressHUD mProgressHUD;
        mProgressHUD = ProgressHUD.show(AddrEdit.this, getResources().getString(R.string.prompt_progress_saving), true, false,
                null);
        String url;
        if (isEdit) {
            url = NetInterface.HOST + NetInterface.METHON_ADDR_UPDATE;
        } else {
            url = NetInterface.HOST + NetInterface.METHON_ADDR_ADD;
        }

        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(TempDataManager.getInstance(getApplicationContext()).getCurrentUid());
        postEntity.setIsLogin(TempDataManager.getInstance(getApplicationContext()).getLoginState());
        postEntity.setName(etName.getText().toString());
        if (etGender.getText().toString().equals(getResources().getString(R.string.gender_male))) {
            postEntity.setSex("1");
        } else {
            postEntity.setSex("2");
        }
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
                        Gson gson = gsonBuilder.create();
                        ResultEntity<String> resultEntity = gson.fromJson(jsonObject.toString(),
                                new TypeToken<ResultEntity<String>>() {
                                }.getType());
                        if (resultEntity.isSuccess()) {
                            Toast.makeText(getApplicationContext(), resultEntity.getResult(), Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            try {
                                Toast.makeText(getApplicationContext(), resultEntity.getError(), Toast.LENGTH_SHORT).show();
                                DebugFlags.logD(TAG, resultEntity.getError());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == UserInfoFix.INTENT_FLAG_GENDER_REQ) {
                String backSring = data.getStringExtra(UserInfoFix.RETURN_BACK_STRING_KEY);
                etGender.setText(backSring);
            }
        }
    }
}
