package com.beetron.outmall.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.beetron.outmall.R;
import com.beetron.outmall.WebViewActivity;
import com.beetron.outmall.utils.DebugFlags;

public class CustomWebView extends WebView{
	private static final String TAG = CustomWebView.class.getSimpleName();
	private ProgressBar progressbar;
	private NaviChanageListener callback = new NaviChanageListener() {
		@Override
		public void backToRoot() {

		}

		@Override
		public void toChild() {

		}
	};

	public CustomWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (context instanceof NaviChanageListener){
			callback = (NaviChanageListener) context;
		}
		progressbar =  new ProgressBar(context,  null, android.R.attr.progressBarStyleHorizontal);
		progressbar.setProgressDrawable(getResources().getDrawable(R.drawable.web_progressbar_color));
		progressbar.setLayoutParams( new LayoutParams(LayoutParams.MATCH_PARENT, 3, 0, 0));
		addView(progressbar);
		//         setWebViewClient(new WebViewClient(){});
		setWebChromeClient( new WebChromeClient());
	}

	public  class WebChromeClient  extends android.webkit.WebChromeClient {
		@Override
		public  void onProgressChanged(WebView view,  int newProgress) {
			if (newProgress == 100) {
				progressbar.setVisibility(GONE);
			}  else {
				if (progressbar.getVisibility() == GONE)
					progressbar.setVisibility(VISIBLE);
				progressbar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}

	}

	@Override
	public boolean canGoForward() {
		DebugFlags.logD(TAG, "canGoForward");
		return super.canGoForward();
	}

	@Override
	public boolean canGoBack() {
		DebugFlags.logD(TAG, "canGoBack");
		return super.canGoBack();
	}

	@Override
	protected  void onScrollChanged( int l,  int t,  int oldl,  int oldt) {
		LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
		lp.x = l;
		lp.y = t;
		progressbar.setLayoutParams(lp);
		super.onScrollChanged(l, t, oldl, oldt);
	}

	public void setNaviListener(NaviChanageListener listener){
		callback = listener;
	}

	public interface NaviChanageListener{
		void backToRoot();
		void toChild();
	}
}