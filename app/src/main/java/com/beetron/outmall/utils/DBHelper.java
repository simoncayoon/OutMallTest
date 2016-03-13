package com.beetron.outmall.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.beetron.outmall.ObserveUtil.CountReciver;
import com.beetron.outmall.OutMallApp;
import com.beetron.outmall.models.DaoSession;
import com.beetron.outmall.models.ProSummary;
import com.beetron.outmall.models.ProSummaryDao;
import com.beetron.outmall.models.UserInfoModel;
import com.beetron.outmall.models.UserInfoModelDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by DKY on 2016/1/21.
 */
public class DBHelper {

    public static final String FLAG_PROSUMMARY_BY_SID = "FLAG_PROSUMMARY_BY_SID";
    public static final String FLAG_PROSUMMARY_BY_FID = "FLAG_PROSUMMARY_BY_FID";
    private static final String TAG = DBHelper.class.getSimpleName();
    private static DBHelper instance = null;
    private static Context mContext = null;
    private static DaoSession daoSession;

    private ProSummaryDao proSummaryDao;
    private UserInfoModelDao userInfoModelDao;

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
            instance.userInfoModelDao = daoSession.getUserInfoModelDao();
        }
        return instance;
    }

    /**
     * 逐个加入购物车，没有批量导入，所以修改商品的总数量
     *
     * @param proSummary
     * @return
     */
    public boolean addShopCart(ProSummary proSummary) {

        DebugFlags.logD(TAG, "当前该商品的数量是：" + proSummary.getCount());

        Long resultCode = proSummaryDao.insertOrReplace(proSummary);
        if (resultCode != -1L) {
            sendBoradCast();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 逐个删除操作
     *
     * @param proSummary
     */
    public void deleteProByOne(ProSummary proSummary) {

        QueryBuilder<ProSummary> qb = proSummaryDao.queryBuilder();
        qb.where(ProSummaryDao.Properties.Sid.eq(proSummary.getSid()));

        List<ProSummary> list = qb.list();
        for (ProSummary item : list) {
            proSummary.setCount(item.getCount() - 1);//增加数量
        }
        try {
            if (proSummary.getCount() > 0) {//当前购物车还有值
                proSummaryDao.insertOrReplace(proSummary);
            } else if (proSummary.getCount() == 0) {//减少到零，删除之
                proSummaryDao.delete(proSummary);
            }
            sendBoradCast();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getShopCartCount() {
        QueryBuilder<ProSummary> qb = proSummaryDao.queryBuilder();
        int count = 0;
        for (ProSummary proSummary : qb.list()) {
            count += proSummary.getCount();
        }
        return count;
    }

    public int getShopCartCounById(String flag, String proId) {
        QueryBuilder<ProSummary> qb = proSummaryDao.queryBuilder();
        if (flag.equals(FLAG_PROSUMMARY_BY_SID)) {
            qb.where(ProSummaryDao.Properties.Sid.eq(proId));
        } else if (flag.equals(FLAG_PROSUMMARY_BY_FID)) {
            qb.where(ProSummaryDao.Properties.Fid.eq(proId));
        }

        int count = 0;
        for (ProSummary proSummary : qb.list()) {
            count += proSummary.getCount();
        }
        return count;
    }

    public synchronized void saveShopLocal(List<ProSummary> dataShopCart) {
        if (dataShopCart == null)
            return;
        try {
            proSummaryDao.insertOrReplaceInTx(dataShopCart);
            sendBoradCast();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ProSummary> getShopCartList() {
        List<ProSummary> result = new ArrayList<ProSummary>();
        QueryBuilder<ProSummary> qb = proSummaryDao.queryBuilder();
        for (ProSummary item : qb.list()) {
            item.setIsSelect(true);//默认宣布选中
            result.add(item);
        }
        return result;
    }

    public Map<String, String> getFidCache(boolean isLimit) {
        QueryBuilder<ProSummary> qb = proSummaryDao.queryBuilder();
//        qb.where(ProSummaryDao.Properties.IsLimit.eq(isLimit));//目前商品的种类没有分开

        HashMap<String, String> map = new HashMap<String, String>();
        for (ProSummary proSummary : qb.list()) {
            map.put(proSummary.getFid(), "");//map 保存不同的fid
        }
        return map;
    }

    /**
     * 购物车整项删除
     *
     * @param sid
     */
    public void deleteShopById(String sid) {
        try {
            proSummaryDao.deleteByKey(sid);
            sendBoradCast();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存用户数据
     *
     * @param userInfoModel
     */
    public void saveUserInfo(UserInfoModel userInfoModel) {

        userInfoModelDao.insertOrReplace(userInfoModel);
    }

    /**
     * 获取本地用户数据
     *
     * @return
     */
    public UserInfoModel getUserInfo() {
        String currUid = TempDataManager.getInstance(mContext).getCurrentUid();
        if (TextUtils.isEmpty(currUid)) {
            return new UserInfoModel();
        } else {
            return userInfoModelDao.load(currUid);
        }
    }

    public void clearShopCart() {
        proSummaryDao.deleteAll();
    }

    public void clearUserInfo() {
        userInfoModelDao.deleteAll();
    }

    void sendBoradCast() {
        Intent intent = new Intent();
        intent.setAction(CountReciver.COUNT_CHANGE_NOTIFICATION_ACTION);
        mContext.sendBroadcast(intent);
    }
}
