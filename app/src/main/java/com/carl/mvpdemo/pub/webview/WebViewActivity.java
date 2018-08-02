package com.carl.mvpdemo.pub.webview;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.carl.mvpdemo.R;
import com.carl.mvpdemo.pub.base.BaseActivity;
import com.carl.mvpdemo.pub.base.BasePresenter;
import com.carl.mvpdemo.pub.utils.LogUtils;
import com.carl.mvpdemo.pub.utils.SPKeyUtils;
import com.carl.mvpdemo.pub.utils.SPUtils;

public class WebViewActivity extends BaseActivity {
    public int mType;

    public String mCookie;

    WebView mWebView;

    public String mLoadUrl;

    public static final String EXTRA_URL = "url";

    public static final String EXTRA_TYPE = "type";

    public static final String EXTRA_COOKIE = "cookie";

    public static final int TYPE_GET_COOKIE = 0x001;

    public static final int TYPE_MANUAL = 0x002;

    public static final int TYPE_WEB = 0x003;

    public static void startActivity(Context context, String url, int type) {
        startActivity(context, url, type, "");
    }

    public static void startActivity(Context context, String url, int type, String cookie) {
        Intent intent = new Intent(context.getApplicationContext(), WebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_COOKIE, cookie);
        context.startActivity(intent);
    }


    @Override
    protected void findView() {
        mWebView = findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new MyWebViewClient());
    }


    @Override
    protected void initData() {
        mLoadUrl = getIntent().getStringExtra(EXTRA_URL);
        mType = getIntent().getIntExtra(EXTRA_TYPE, 0);
        mCookie = getIntent().getStringExtra(EXTRA_COOKIE);
        if (mType == TYPE_WEB) {
            syncCookie();
        }
        mWebView.loadUrl(mLoadUrl);
    }


    public void syncCookie() {
        CookieManager cookieManager = CookieManager.getInstance();
        String oldCookie = cookieManager.getCookie(mLoadUrl);
        LogUtils.e("oldCookie:" + oldCookie);
        cookieManager.setCookie(mLoadUrl, mCookie);
        String newCookie = cookieManager.getCookie(mLoadUrl);
        LogUtils.e("newCookie:" + newCookie);
    }


    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView webview, String url) {
            webview.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            CookieManager cookieManager = CookieManager.getInstance();
            String cookieStr = cookieManager.getCookie(url);
            if (!TextUtils.isEmpty(cookieStr)) {
                LogUtils.e("Cookie", cookieStr);
                SPUtils.getInstance().put(SPKeyUtils.COOKIE, cookieStr);
            }
            //获取cookie自动关闭页面
            if (mType == TYPE_GET_COOKIE) {
                finish();
            }
            super.onPageFinished(view, url);
        }

    }


    @Override
    protected void initView() {

    }

    @Override
    public void setOnInteractListener() {

    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }
}
