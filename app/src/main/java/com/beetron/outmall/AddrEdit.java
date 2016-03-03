package com.beetron.outmall;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
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
import com.beetron.outmall.models.AddrInfoModel;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.utils.BooleanSerializer;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
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
    private Boolean isEdit = true;
    private AddrInfoModel postEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addr_edit_layout);
        initView();
        postEntity = getIntent().getParcelableExtra(INTENT_KEY_ADDR_EDIT);
        if (postEntity == null) {//如果没有值，表示为新增地址
            postEntity = new AddrInfoModel();
            isEdit = false;
        } else {
            try {
                initData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initData() throws Exception{
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
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Toast.makeText(AddrEdit.this, getResources().getString(R.string.prompt_addr_name_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etPhone.getText().toString())) {
            Toast.makeText(AddrEdit.this, getResources().getString(R.string.prompt_addr_phone_empty),
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
                        if (resultEntity.isSuccess()) {
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
