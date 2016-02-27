package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beetron.outmall.models.OrderFixInfo;
import com.beetron.outmall.models.UserInfoModel;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.TempDataManager;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/27.
 * Time: 10:40.
 */
public class UserInfo extends Activity implements View.OnClickListener {

    private static final String TAG = UserInfo.class.getSimpleName();
    UserInfoModel userInfoSummary;
    private TextView tvNickName, tvSig, tvGender, tvSchool;
    private LinearLayout llPortrait, llSign, llGender, llSchool, llNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_layout);
        userInfoSummary = new UserInfoModel();
        userInfoSummary.setProvince("1");
        userInfoSummary.setArea("1");
        userInfoSummary.setCity("1");
        userInfoSummary.setMail("");
        initView();

        try {
            getUserInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUserInfo() throws Exception {

    }

    private void initView() {
        llPortrait = (LinearLayout) findViewById(R.id.ll_portrait_layout);
        llNickName = (LinearLayout) findViewById(R.id.ll_nick_name_layout);
        llSign = (LinearLayout) findViewById(R.id.ll_signature_layout);
        llGender = (LinearLayout) findViewById(R.id.ll_gender_layout);
        llSchool = (LinearLayout) findViewById(R.id.ll_school_layout);

        tvNickName = (TextView) findViewById(R.id.user_info_nick_name);
        tvSig = (TextView) findViewById(R.id.user_info_signature);
        tvGender = (TextView) findViewById(R.id.user_info_gender);
        tvSchool = (TextView) findViewById(R.id.user_info_school);

        llNickName.setOnClickListener(this);
        llSign.setOnClickListener(this);
        llGender.setOnClickListener(this);
        llSchool.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(UserInfo.this, UserInfoFix.class);
        if (v.getId() == R.id.ll_nick_name_layout) {
            intent.putExtra(UserInfoFix.INTENT_KEY, UserInfoFix.INTENT_FLAG_NICKNAME_REQ);
            startActivityForResult(intent, UserInfoFix.INTENT_FLAG_NICKNAME_REQ);
            return;
        }
        if (v.getId() == R.id.ll_signature_layout) {
            intent.putExtra(UserInfoFix.INTENT_KEY, UserInfoFix.INTENT_FLAG_SIG_REQ);
            startActivityForResult(intent, UserInfoFix.INTENT_FLAG_SIG_REQ);
            return;
        }
        if (v.getId() == R.id.ll_gender_layout) {
            intent.putExtra(UserInfoFix.INTENT_KEY, UserInfoFix.INTENT_FLAG_GENDER_REQ);
            startActivityForResult(intent, UserInfoFix.INTENT_FLAG_GENDER_REQ);
            return;
        }
        if (v.getId() == R.id.ll_school_layout) {
            intent.putExtra(UserInfoFix.INTENT_KEY, UserInfoFix.INTENT_FLAG_SCHOOL_REQ);
            startActivityForResult(intent, UserInfoFix.INTENT_FLAG_SCHOOL_REQ);
            return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        DebugFlags.logD(TAG, "");
        if (resultCode == RESULT_OK) {

            String backSring = data.getStringExtra(UserInfoFix.RETURN_BACK_STRING_KEY);
            if (requestCode == UserInfoFix.INTENT_FLAG_NICKNAME_REQ) {
                tvNickName.setText(backSring);
                userInfoSummary.setNickname(backSring);
            }
            if (requestCode == UserInfoFix.INTENT_FLAG_SIG_REQ) {
                TempDataManager.getInstance(UserInfo.this).setUserSig(backSring);
                tvSig.setText(backSring);
            }
            if (requestCode == UserInfoFix.INTENT_FLAG_GENDER_REQ) {
                tvGender.setText("男");
                userInfoSummary.setSex("1");
            }
            if (requestCode == UserInfoFix.INTENT_FLAG_SCHOOL_REQ) {
                tvSchool.setText(backSring);
                userInfoSummary.setAddress(backSring);
            }
        }
    }
}
