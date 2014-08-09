package org.zju.cadcg.watao.activity;

import org.zju.cadcg.watao.R;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class IdeaMarketActivity extends Activity {

    private WebView mWebView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_market);
        
        mWebView = (WebView) findViewById(R.id.idea_market_webview);
        
        // to make the text auto size to work
        setUpWebViewDefaults(mWebView);
        

        // Make the WebView handle all loaded URLs
        mWebView.setWebViewClient(new WebViewClient());

        mWebView.loadUrl("http://www.baidu.com");
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
    }
    
    /**
     * Convenience method to set some generic defaults for a
     * given WebView
     *
     * @param webView
     */
    @SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();
        
        // Enable Javascript
        settings.setJavaScriptEnabled(true);

        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }
        
        // Enable remote debugging via chrome://inspect
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }
    
}
