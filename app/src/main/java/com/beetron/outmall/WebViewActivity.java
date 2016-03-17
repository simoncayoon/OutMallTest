package com.beetron.outmall;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.customview.CustomWebView;
import com.beetron.outmall.customview.ProgressHUD;

/**
 * Created by luomaozhong on 16/3/2.
 */
public class WebViewActivity extends Activity {
    private CustomWebView wv;
    private ProgressHUD mProgressHUD;
    private String tag = "WebViewActivity";
    private String title = "";
    private String url = "";

    private CusNaviView cusNaviView;

    private void initNavi() {
        title = getIntent().getExtras().getString("title");
        url = getIntent().getExtras().getString("url");
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        cusNaviView.setBtn(CusNaviView.PUT_BACK_ENABLE, CusNaviView.NAVI_WRAP_CONTENT, 56);
        cusNaviView.setBtn(CusNaviView.PUT_RIGHT, CusNaviView.NAVI_WRAP_CONTENT, 56);
        cusNaviView.setNaviTitle(title);

        ((Button) cusNaviView.getLeftBtn()).setText("返回");//设置返回标题

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);
        initNavi();
        mProgressHUD = ProgressHUD.show(this, "正在加载...", true, false,
                null);
        /*
		 * // 生成水平进度条 progressBar = new ProgressBar(this,
		 * null,android.R.attr.progressBarStyleHorizontal);
		 * progressBar.setProgressDrawable
		 * (getResources().getDrawable(R.drawable.progressbar_style));
		 * progressBar.setMinimumHeight(1);
		 */
        wv = (CustomWebView) findViewById(R.id.wv_tao);
        wv.setBackgroundColor(getResources().getColor(R.color.white));
        // wv.setBackgroundResource(R.drawable.all_bg);
        // rootViewLayout.addView(progressBar, new
        // LayoutParams(LayoutParams.FILL_PARENT, R.dimen.progressbar_height));
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setJavaScriptEnabled(true);
        // 缩放开关
        wv.getSettings().setSupportZoom(false);
        // 设置是否可缩放
        wv.getSettings().setBuiltInZoomControls(false);
        wv.getSettings().setLoadWithOverviewMode(false);
        // 无限缩放
        wv.getSettings().setUseWideViewPort(false);
        // 设置本地缓存
        wv.getSettings().setDomStorageEnabled(true);
        // wv.getSettings().setDomStorageEnabled(true);
        // wv.getSettings().setAppCacheEnabled(true);
		/*
		 * if (Build.VERSION.SDK_INT >= 19) {
		 * wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); }
		 */
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(tag, "调用onPageFinished");
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                // 页面下载完毕,却不代表页面渲染完毕显示出来
                // WebChromeClient中progress==100时也是一样
                if (wv.getContentHeight() != 0) {
                    // 这个时候网页才显示
//                    mLl_loading.setVisibility(View.GONE);// 隐藏加载框
                }

            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                Log.d(tag, "调用shouldOverrideKeyEvent");
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.d(tag, "调用onLoadResource");
                if (wv.getContentHeight() != 0) {
                    // 这个时候网页才显示
                    //mLl_loading.setVisibility(View.GONE);// 隐藏加载框
                }
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {

                Log.d(tag, "调用onReceivedError");
                wv.setVisibility(View.GONE);
                Toast.makeText(WebViewActivity.this, "加载出错！", Toast.LENGTH_LONG).show();
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                super.onProgressChanged(view, newProgress);
                // 这里将textView换成你的progress来设置进度

                if (newProgress == 100) {
                    mProgressHUD.hide();
                }
            }
        });
        wv.loadUrl(url);
    }

}
