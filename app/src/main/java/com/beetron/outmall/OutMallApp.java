package com.beetron.outmall;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.beetron.outmall.models.DaoMaster;
import com.beetron.outmall.models.DaoSession;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/1/23.
 * Time: 17:17.
 */
public class OutMallApp extends Application {
    private static OutMallApp ourInstance = new OutMallApp();


    private DaoMaster daoMaster = null;
    private DaoSession mDaoSession = null;
    private SQLiteDatabase db;

    public static OutMallApp getInstance() {
        return ourInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ourInstance = this;
        initDao();
    }

    private void initDao() {
        if (daoMaster == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "outmall-db", null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
        }
    }

    public DaoMaster getDaoMaster() {
        if (daoMaster == null) {
            initDao();
        }
        return daoMaster;
    }

    public DaoSession getDaoSession() {
        if (mDaoSession == null) {
            if (daoMaster == null) {
                mDaoSession = getDaoMaster().newSession();
            }
            mDaoSession = daoMaster.newSession();
        }
        return mDaoSession;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
