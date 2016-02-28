package com.beetron.outmall;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.beetron.outmall.customview.CusNaviView;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/21.
 * Time: 17:00.
 */
public class LoginFastActivity extends Activity {

    private CusNaviView cusNaviView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_fast_layout);

        initView();
    }

    private void initView() {
        initNavi();
    }

    private void initNavi() {
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        cusNaviView.setNaviTitle(getResources().getString(R.string.navi_title_login_fast));
        cusNaviView.setBtn(CusNaviView.PUT_BACK_ENABLE, CusNaviView.NAVI_WRAP_CONTENT, 56);

        ((Button) cusNaviView.getLeftBtn()).setText(getResources().getString(R.string.navi_title_login));//设置返回标题

        cusNaviView.setNaviBtnListener(new CusNaviView.NaviBtnListener() {
            @Override
            public void leftBtnListener() {
                finish();
            }

            @Override
            public void rightBtnListener() {

            }
        });
    }
}
