package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beetron.outmall.utils.DebugFlags;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/27.
 * Time: 15:49.
 */
public class UserInfoFix extends Activity{

    public static final int INTENT_FLAG_NICKNAME_REQ = 0x11;
    public static final String INTENT_KEY = "INTENT_KEY";
    public static final int INTENT_FLAG_SIG_REQ = 0x22;
    public static final int INTENT_FLAG_GENDER_REQ = 0x33;
    public static final int INTENT_FLAG_SCHOOL_REQ = 0x44;
    public static final String RETURN_BACK_STRING_KEY = "RETURN_BACK_STRING_KEY";
    private static final String INTENT_INIT_DATA = "INTENT_INIT_DATA";
    private static final String TAG = UserInfoFix.class.getSimpleName();
    String naviTitle;
    private EditText etFix;
    private Button btnTest;
    private String initString = "";
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_info_item_layout);

        flag = getIntent().getIntExtra(INTENT_KEY, 0);

        etFix = (EditText) findViewById(R.id.et_order_info_item);
        btnTest = (Button) findViewById(R.id.test_btn);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBack = new Intent();
                intentBack.putExtra(RETURN_BACK_STRING_KEY, etFix.getText().toString());
                setResult(RESULT_OK, intentBack);
                finish();
            }
        });

        initString = getIntent().getStringExtra(INTENT_INIT_DATA);
        etFix.setText(initString);

        initData();
    }

    private void initData() {
        if (flag == INTENT_FLAG_NICKNAME_REQ) {
            naviTitle = getResources().getString(R.string.user_info_title_nick);
        }
        if (flag == INTENT_FLAG_SIG_REQ) {
            naviTitle = getResources().getString(R.string.user_info_title_signature);
        }
        if (flag == INTENT_FLAG_GENDER_REQ) {
            naviTitle = getResources().getString(R.string.user_info_title_gender);
        }
        if (flag == INTENT_FLAG_SCHOOL_REQ) {
            naviTitle = getResources().getString(R.string.user_info_school);
        }

        DebugFlags.logD(TAG, "当前标题：" + naviTitle);
    }

}
