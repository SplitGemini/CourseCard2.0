package com.example.hasee.coursecard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class CASActivity extends AppCompatActivity {
    static public WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cas);
        webView = findViewById(R.id.webView);
        getInfoFromWeb();
    }

    public void init() {
        //
    }

    @Override
    protected void onDestroy(){
        webView.goBack();
        super.onDestroy();
    }

    public void getInfoFromWeb() {
//        Log.d("msg",Integer.toString(checkSelfPermission(Manifest.permission.INTERNET)));
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        //这个登录界面还能用，但是api已经失效了所以登录了也没用
        //webView.loadUrl("https://cas.sysu.edu.cn/cas/login?service=https%3A%2F%2Fuems.sysu.edu.cn%2Fjwxt%2Fapi%2Fsso%2Fcas%2Flogin%3Fpattern%3Dstudent-login");
        webView.loadUrl("https://splitgemini.github.io/Description.html");

        webView.setWebChromeClient(new WebChromeClient());

        //api is deprecated
        //webView.setWebViewClient(new MyWebViewClient(CASActivity.this));//覆盖访问
        webView.setWebViewClient(new MyWebViewClientToGithub(CASActivity.this));//覆盖访问
    }
}
