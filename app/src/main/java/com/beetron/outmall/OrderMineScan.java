package com.beetron.outmall;

import android.app.Activity;
import android.os.Bundle;

import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.viewpager.SViewPager;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/27.
 * Time: 22:38.
 */
public class OrderMineScan extends Activity{

    private IndicatorViewPager indicatorViewPager;
    private FixedIndicatorView indicatorView;
    private SViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_mine_scan_layout);

        initView();
    }

    private void initView() {
        indicatorView = (FixedIndicatorView) findViewById(R.id.indicate_order_status_bar);
        viewPager = (SViewPager) findViewById(R.id.viewpager_order_data_with_status);
        indicatorViewPager  = new IndicatorViewPager(indicatorView, viewPager);
//        indicatorViewPager.setAdapter(new );
    }
}
