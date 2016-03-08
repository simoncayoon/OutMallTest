package com.beetron.outmall;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/3/7.
 * Time: 21:39.
 */
public class ProWebFragment extends BaseFragment {

    private final String HTML_ENCODING = "UTF-8";
    private final String HTML_MIME_TYPE = "text/html";
    WebView contentView;
    String content = "";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.pro_web_view_layout);
        content = getArguments().getString(INTENT_STRING_TABNAME);
        contentView = (WebView) findViewById(R.id.pro_detail_descript);
        if (getArguments().getInt(INTENT_INT_INDEX) == 1) {
            contentView.getSettings().setJavaScriptEnabled(true);
            contentView.getSettings().setSupportZoom(true);
            contentView.getSettings().setUseWideViewPort(true);
            contentView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            contentView.getSettings().setLoadWithOverviewMode(true);
        }

        contentView.loadDataWithBaseURL("file://", content, HTML_MIME_TYPE, HTML_ENCODING, "about:blank");
    }
}
