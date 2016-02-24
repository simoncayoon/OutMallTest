package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/22.
 * Time: 18:04.
 */
public class AddrManager extends Activity {

    private ListView llAddrList;
    private Button btnToadd;
    private LinearLayout llEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_manage_layout);
        initView();
    }

    private void initView() {
        llAddrList = (ListView) findViewById(R.id.shop_cart_detail_list);
        llEmpty = (LinearLayout) findViewById(R.id.addr_manage_empty_layout);
        btnToadd = (Button) findViewById(R.id.btn_add_addr_info);
        btnToadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddrManager.this, AddrEdit.class));
            }
        });
    }
}
