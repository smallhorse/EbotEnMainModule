package com.ubt.en.alpha1e.webview;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.en.alpha1e.R;
import com.vise.log.ViseLog;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class UserServiceActivity extends AppCompatActivity {

    private WebView mWebView;
    private String URL= "";
    public static final String USER_CODE = "user_code";
    public static final int USER_SERVICE_CODE = 1;  //用户服务协议
    public static final int USER_PRIVACY_CODE = 2;  //用户隐私协议
    private int code = 1;

    private TextView tvTitle;
    private ImageView ivBack;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_service);
        mWebView = (WebView) findViewById(R.id.wb_user_service);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        code = getIntent().getIntExtra(USER_CODE, 1);
        if(code == USER_SERVICE_CODE){
            URL = "http://10.10.1.14:8080/alpha1e/gdpr/service.html?systemLanguage=CN";
            tvTitle.setText("Terms of Service");
        }else {
            URL = "http://10.10.1.14:8080/alpha1e/gdpr/procy.html?systemLanguage=CN";
            tvTitle.setText("Privacy policy");
        }
        ViseLog.d("URL:" + URL);
        initWebView();
    }


    private void initWebView(){
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        //开发稳定后需去掉该行代码
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setUseWideViewPort(true);  //将图片调整到适合webview的大小
        mWebView.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


            }

        };

        mWebView.setWebViewClient(webViewClient);
        mWebView.loadUrl(URL);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }




}
