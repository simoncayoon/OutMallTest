package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/22.
 * Time: 14:45.
 */
public class OrderFixActivity extends Activity {

    private LinearLayout llToAddrMng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_fix_layout);

        initView();
    }

    private void initView() {
        llToAddrMng = (LinearLayout) findViewById(R.id.ll_order_fix_to_address);

        llToAddrMng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderFixActivity.this, AddrManager.class));
            }
        });
    }
}
