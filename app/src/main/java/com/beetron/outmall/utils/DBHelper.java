package com.beetron.outmall.utils;

import android.content.Context;

import com.beetron.outmall.OutMallApp;
import com.beetron.outmall.models.DaoSession;
import com.beetron.outmall.models.ProSummary;
import com.beetron.outmall.models.ProSummaryDao;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by DKY on 2016/1/21.
 */
public class DBHelper {

    public static final String FLAG_DISTRICT = "flag_district";
    public static final String FLAG_TOWN = "flag_town";
    public static final String FLAG_ADM_COUNTRY = "flag_adm_country";
    public static final String FLAG_GROUP = "flag_group";
    private static final String TAG = DBHelper.class.getSimpleName();
    private static DBHelper instance = null;
    private static Context mContext = null;
    private static DaoSession daoSession;

    private ProSummaryDao proSummaryDao;

    private DBHelper() {
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper();
            if (mContext == null) {
                mContext = context;
            }
            daoSession = OutMallApp.getInstance().getDaoSession();
            instance.proSummaryDao = daoSession.getProSummaryDao();
        }
        return instance;
    }

    public Long addShopCart(ProSummary proSummary){
        return proSummaryDao.insert(proSummary);
    }

    public int getShopCartCount(){
        QueryBuilder<ProSummary> qb = proSummaryDao.queryBuilder();
        return qb.list().size();
    }
}
