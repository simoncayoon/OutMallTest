package com.beetron.outmall.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class CustomWebView extends WebView {
	public ScrollInterface mScrollInterface;

	public CustomWebView(Context context) {
		super(context);
	}

	public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {

		super.onScrollChanged(l, t, oldl, oldt);

		mScrollInterface.onSChanged(l, t, oldl, oldt);

	}

	public void setOnCustomScroolChangeListener(ScrollInterface scrollInterface) {

		this.mScrollInterface = scrollInterface;

	}

	public interface ScrollInterface {

		public void onSChanged(int l, int t, int oldl, int oldt);

	}

}