package com.beetron.outmall;

import android.os.Bundle;
import android.view.View;

import com.beetron.outmall.utils.ShopCartChangReceiver;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/3.
 * Time: 15:31.
 */
public class ShopLimit extends BaseFragment{

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setContentView(R.layout.shop_limit_layout);
    }
}
