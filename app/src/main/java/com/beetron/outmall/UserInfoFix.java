package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.beetron.outmall.adapter.SexAdapter;
import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.utils.CommonHelper;
import com.beetron.outmall.utils.DebugFlags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/27.
 * Time: 15:49.
 */
public class UserInfoFix extends Activity {

    public static final int INTENT_FLAG_NICKNAME_REQ = 0x11;
    public static final String INTENT_KEY = "INTENT_KEY";
    public static final String INTENT_VALUE = "INTENT_VALUE";
    public static final int INTENT_FLAG_EMAIL_REQ = 0x22;
    public static final int INTENT_FLAG_GENDER_REQ = 0x33;
    public static final int INTENT_FLAG_PROVINCE_REQ = 0x44;
    public static final int INTENT_FLAG_CITY_REQ = 0x55;
    public static final int INTENT_FLAG_AREA_REQ = 0x66;
    public static final int INTENT_FLAG_STREET_REQ = 0x77;
    public static final int INTENT_FLAG_HEAD_REQ = 0x88;
    public static final String RETURN_BACK_STRING_KEY = "RETURN_BACK_STRING_KEY";
    private static final String INTENT_INIT_DATA = "INTENT_INIT_DATA";
    private static final String TAG = UserInfoFix.class.getSimpleName();
    String naviTitle;
    private EditText etFix;
    private String initString = "";
    private int flag;
    private String value;
    private CusNaviView cusNaviView;
    public static final String FLAG_NAVI_ROOT = "FLAG_NAVI_ROOT";

    private ListView mlistview;
    private LinearLayout ll_layout;
    private SexAdapter adapter;
    private List<HashMap<String, String>> mlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_info_item_layout);
        flag = getIntent().getIntExtra(INTENT_KEY, 0);
        value = getIntent().getStringExtra(INTENT_VALUE);
        etFix = (EditText) findViewById(R.id.et_order_info_item);

        initString = getIntent().getStringExtra(INTENT_INIT_DATA);
        etFix.setText(initString);

        initData();
        initNavi();
    }

    private void initData() {
        if (flag == INTENT_FLAG_NICKNAME_REQ) {
            naviTitle = getResources().getString(R.string.user_info_title_nick);
        }
        if (flag == INTENT_FLAG_EMAIL_REQ) {
            naviTitle = getResources().getString(R.string.user_info_email);
        }
        if (flag == INTENT_FLAG_GENDER_REQ) {
            naviTitle = getResources().getString(R.string.user_info_title_gender);
            //性别设置
            mlistview = (ListView) findViewById(R.id.listView);
            ll_layout = (LinearLayout) findViewById(R.id.ll_layout);
            ll_layout.setVisibility(View.VISIBLE);
            initDataSex();
            adapter = new SexAdapter(UserInfoFix.this, mlist);
            mlistview.setAdapter(adapter);
            mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        value = getResources().getString(R.string.gender_male);
                    } else {
                        value = getResources().getString(R.string.gender_femal);
                    }
                    initDataSex();
                    adapter.upData(mlist);
                    adapter.notifyDataSetChanged();
                }
            });
        } else {
            etFix.setText(value);
            etFix.setVisibility(View.VISIBLE);
            CharSequence text = etFix.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable) text;
                Selection.setSelection(spanText, text.length());
            }
        }

        if (flag == INTENT_FLAG_PROVINCE_REQ) {
            naviTitle = getResources().getString(R.string.user_info_province);
        }

        if (flag == INTENT_FLAG_CITY_REQ) {
            naviTitle = getResources().getString(R.string.user_info_city);
        }


        if (flag == INTENT_FLAG_AREA_REQ) {
            naviTitle = getResources().getString(R.string.user_info_area);
        }


        if (flag == INTENT_FLAG_STREET_REQ) {
            naviTitle = getResources().getString(R.string.user_info_street);
        }


        DebugFlags.logD(TAG, "当前标题：" + naviTitle);
    }

    private void initDataSex() {
        mlist = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "1");
        if (value.equals("男")) {
            map.put("checked", "yes");
        } else {
            map.put("checked", "no");
        }
        mlist.add(map);
        map = new HashMap<>();
        map.put("name", "0");
        if (value.equals("女")) {
            map.put("checked", "yes");
        } else {
            map.put("checked", "no");
        }
        mlist.add(map);
    }

    private void initNavi() {
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        cusNaviView.setNaviTitle(getResources().getString(R.string.navi_title_login));
        cusNaviView.setBtn(CusNaviView.PUT_BACK_ENABLE, CusNaviView.NAVI_WRAP_CONTENT, 56);
        cusNaviView.setBtn(CusNaviView.PUT_RIGHT, CusNaviView.NAVI_WRAP_CONTENT, 56);
        cusNaviView.setNaviTitle(naviTitle);

        ((Button) cusNaviView.getLeftBtn()).setText(getResources().getString(R.string.cancel));//设置返回标题
        ((Button) cusNaviView.getRightBtn()).setText(getResources().getString(R.string.complete));//设置返回标题

        cusNaviView.setNaviBtnListener(new CusNaviView.NaviBtnListener() {
            @Override
            public void leftBtnListener() {
                finish();
            }

            @Override
            public void rightBtnListener() {
                String result = "";
                if (flag == INTENT_FLAG_GENDER_REQ) {
                    result = value;
                    if(result.equals("")){
                        Toast.makeText(UserInfoFix.this, "请先选择性别！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    result = etFix.getText().toString();
                    if(flag==INTENT_FLAG_EMAIL_REQ && !CommonHelper.checkMail(result)){
                        Toast.makeText(UserInfoFix.this, "请先填写正确的"+naviTitle+"！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(result.equals("")){
                        Toast.makeText(UserInfoFix.this, "请先填写"+naviTitle+"！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Intent intentBack = new Intent();
                intentBack.putExtra(RETURN_BACK_STRING_KEY, result);
                setResult(RESULT_OK, intentBack);
                finish();
            }
        });
    }


}
