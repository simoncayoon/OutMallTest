package com.beetron.outmall.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.models.UserInfoModel;

public class TempDataManager {

    public static final String TAG = TempDataManager.class.getSimpleName();
    public static final String SP_USER_SIGNATURE = "SP_USER_SIGNATURE";
    private static final String SP_USER_INFO_UID = "SP_USER_INFO_UID";
    private static final String SP_IS_LOGIN = "SP_IS_LOGIN";
    private static final String SP_USER_NAME = "SP_USER_NAME";
    private static final String SP_USER_TEL = "SP_USER_TEL";
    private static final String SP_IS_UPDATE_FLAG = "SP_IS_UPDATE_FLAG";
    private static TempDataManager instance = null;
    private Context mContext = null;
    private SharedPreferences sp = null;
    private Editor mEditor = null;

    private TempDataManager(Context context) {
        mContext = context;
        sp = mContext.getSharedPreferences(Constants.SP_GENERAL_PROFILE_NAME,
                Context.MODE_PRIVATE);
        mEditor = sp.edit();
    }

    public static TempDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new TempDataManager(context);
        }
        return instance;
    }

    public String getCurrentUid(){
        return sp.getString(SP_USER_INFO_UID, "");
    }

    public void setCurrentUid (String uid){
        mEditor.putString(SP_USER_INFO_UID, uid);
        mEditor.putString(SP_IS_LOGIN, "1");
        mEditor.commit();
    }

    /**
     * 清空当前临时信息
     */
    public void clearCurrentTemp() {
        mEditor.clear();
        mEditor.commit();
    }

    public void setUserSig(String backSring) {
        mEditor.putString(SP_USER_SIGNATURE, backSring);
    }

    public boolean isLogin() {
        return sp.getString(SP_IS_LOGIN, "").equals("1") ? true : false;
    }

    public void setLoginResult(String uid, String uname, String utel) {
        mEditor.putString(SP_USER_INFO_UID, uid);
        mEditor.putString(SP_USER_NAME, uname);
        mEditor.putString(SP_USER_TEL, utel);
        mEditor.putString(SP_IS_LOGIN, "1");
        if (mEditor.commit()) {
            DebugFlags.logD(TAG, "登录状态写入！");
        }
    }

    public String getLoginState() {
        return sp.getString(SP_IS_LOGIN, "");
    }

}
