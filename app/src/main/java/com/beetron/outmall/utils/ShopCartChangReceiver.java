package com.beetron.outmall.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.beetron.outmall.MainActivity;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/3/6.
 * Time: 01:44.
 */
public class ShopCartChangReceiver extends BroadcastReceiver {

    private ShopCartChange instance;

    public ShopCartChangReceiver(ShopCartChange shopCartChange) {
        instance = shopCartChange;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MainActivity.ACTION_STR))
            instance.shopCartDataChange();
    }

    public interface ShopCartChange {
        void shopCartDataChange();
    }
}
