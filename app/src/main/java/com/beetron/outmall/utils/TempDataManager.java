package com.beetron.outmall.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.beetron.outmall.constant.Constants;

public class TempDataManager {

    public static final String TAG = TempDataManager.class.getSimpleName();
    public static final String SP_USER_SIGNATURE = "SP_USER_SIGNATURE";
    private static final String SP_UID = "SP_UID";
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

    String getCurrentUid(){
        return sp.getString(SP_UID, "");
    }

    public void setCurrentUid (String uid){
        mEditor.putString(SP_UID, uid);
        mEditor.commit();
    }

    /**
     * 清空当前临时信息
     */
    public void clearCurrentTemp() {
        mEditor.clear();
        mEditor.commit();
    }

    public String getUserSig() {
        return sp.getString(SP_USER_SIGNATURE, "添加签名，展示您的个性！");
    }


    public void setUserSig(String backSring) {
        mEditor.putString(SP_USER_SIGNATURE, backSring);
    }
}
