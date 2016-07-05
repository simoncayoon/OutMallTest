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
import com.beetron.outmall.utils.DebugFlags;

/**
 * Created by luomaozhong on 16/3/2.
 */
public class WebViewActivity extends Activity {
    private static final String TAG = WebViewActivity.class.getSimpleName();
    public static final java.lang.String WEB_TITLE = "WEB_TITLE";
    public static final java.lang.String WEB_URL = "WEB_URL";
    private CustomWebView wv;
    private String tag = "WebViewActivity";
    private String title = "";
    private String url = "";

    private CusNaviView cusNaviView;

    private void initNavi() {
        title = getIntent().getExtras().getString(WEB_TITLE);
        url = getIntent().getExtras().getString(WEB_URL);
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

        wv = (CustomWebView) findViewById(R.id.wv_tao);
        wv.setBackgroundColor(getResources().getColor(R.color.white));
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

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
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
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (view.canGoForward()){
                    DebugFlags.logD(TAG, "导航栏显示关闭按钮");
                } else if (view.canGoBack()){
                    DebugFlags.logD(TAG, "刷新显示root页面，去掉关闭按钮");
                }
                DebugFlags.logD(TAG, "shouldOverrideUrlLoading " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                if (wv.getContentHeight() != 0) {
                    // 这个时候网页才显示
                    //mLl_loading.setVisibility(View.GONE);// 隐藏加载框
                }
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {

                wv.setVisibility(View.GONE);
                Toast.makeText(WebViewActivity.this, "加载出错！", Toast.LENGTH_LONG).show();
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });
        wv.loadUrl(url);
    }
}
