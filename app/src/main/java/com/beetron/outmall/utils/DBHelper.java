package com.beetron.outmall.utils;

import android.content.Context;

import com.beetron.outmall.OutMallApp;
import com.beetron.outmall.models.DaoSession;
import com.beetron.outmall.models.ProSummary;
import com.beetron.outmall.models.ProSummaryDao;
import com.beetron.outmall.models.ShopCartModel;
import com.beetron.outmall.models.UserInfoModel;
import com.beetron.outmall.models.UserInfoModelDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    public Long addShopCart(ProSummary proSummary) {
//        QueryBuilder<ProSummary> qb = proSummaryDao.queryBuilder();
//        qb.where(ProSummaryDao.Properties.Sid.eq(proSummary.getSid()));
//
//        if (proSummaryDao.load(proSummary.getSid()) != null){//该选项已存在购物车
//            proSummary = proSummaryDao.load(proSummary.getSid());
//            int currCount = proSummary.getCount();
//            proSummary.setCount(currCount ++);
//        } else {
//            proSummary.setCount(1);//初始化该商品的数量
//        }
        DebugFlags.logD(TAG, "当前该商品的数量是：" + proSummary.getCount());
        return proSummaryDao.insertOrReplace(proSummary);
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
        if (proSummary.getCount() > 0) {//当前购物车还有值
            proSummaryDao.insertOrReplace(proSummary);
        } else if (proSummary.getCount() == 0) {//减少到零，删除之
            proSummaryDao.delete(proSummary);
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

    public synchronized void saveShopLocal(List<ShopCartModel> dataShopCart) {
        List<ProSummary> dataList = new ArrayList<ProSummary>();
        if (dataShopCart == null)
            return;
        for (ShopCartModel shopCartModel : dataShopCart) {
            ProSummary proSummary = new ProSummary();
            proSummary.setFid(shopCartModel.getGs().getFid());
            proSummary.setSid(shopCartModel.getSid());
            proSummary.setXl(0);
            proSummary.setPrice1(shopCartModel.getGs().getPrice1());
            proSummary.setPrice2(shopCartModel.getGs().getPrice2());
            proSummary.setTitle(shopCartModel.getGs().getTitle());
            proSummary.setCount(shopCartModel.getNum());
            proSummary.setImg(shopCartModel.getGs().getImg());
            proSummary.setJianshu(shopCartModel.getGs().getJianshu());

            dataList.add(proSummary);
        }
        proSummaryDao.insertOrReplaceInTx(dataList);
    }

    public List<ProSummary> getShopCartList() {
        QueryBuilder<ProSummary> qb = proSummaryDao.queryBuilder();
        return qb.list();
    }

    public Map<String, String> getFidCache(){
        QueryBuilder<ProSummary> qb = proSummaryDao.queryBuilder();


        HashMap<String, String> map = new HashMap<String, String>();
        for (ProSummary proSummary : qb.list()){
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存用户数据
     * @param userInfoModel
     */
    public void saveUserInfo(UserInfoModel userInfoModel) {
        synchronized (userInfoModelDao) {
            userInfoModelDao.deleteAll();
            userInfoModelDao.insertOrReplace(userInfoModel);
        }
    }

    /**
     * 获取本地用户数据
     * @return
     */
    public UserInfoModel getUserInfo(){
        QueryBuilder<UserInfoModel> qb = userInfoModelDao.queryBuilder();
        return qb.list().get(0);
    }
}
