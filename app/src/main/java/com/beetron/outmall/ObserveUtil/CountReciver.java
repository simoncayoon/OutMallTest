package com.beetron.outmall.ObserveUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.beetron.outmall.utils.DebugFlags;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/3/11.
 * Time: 15:08.
 */
public class CountReciver extends BroadcastReceiver {

    public static final String COUNT_CHANGE_NOTIFICATION_ACTION = "com.chulai-mai.action.countchange";
    private static final String TAG = CountReciver.class.getSimpleName();
    private Context mContext;

    public CountReciver(Context context) {
        mContext = context;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(COUNT_CHANGE_NOTIFICATION_ACTION)) {
            if (mContext instanceof UpdateCount) {
                ((UpdateCount) mContext).notifyCountUpdate();
            } else {
                DebugFlags.logD(TAG, "接收到的广播无效！");
            }
        }
    }

    public interface UpdateCount {
        public void notifyCountUpdate();
    }
}
