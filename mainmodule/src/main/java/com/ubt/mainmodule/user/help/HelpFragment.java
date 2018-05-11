package com.ubt.mainmodule.user.help;

import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
import com.vise.log.ViseLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/19 15:21
 * @描述:
 */

public class HelpFragment extends SupportFragment {
    private static final String HELP_URL = "http://10.10.32.22:8080/setHelp_en/setHelp.html?userid=803522&token=1eec5419a5294587b87361a9715d11b2803522";
//    private static final String HELP_URL = "http://www.baidu.com";
//    private static final String HELP_URL = "http://10.10.1.14:8080/alpha1e/setHelp.html";
    @BindView(R2.id.help_web_content)    WebView helpWebContent;
    Unbinder unbinder;
//    private Stack<String> mUrls;


    public static HelpFragment newInstance() {

        Bundle args = new Bundle();

        HelpFragment fragment = new HelpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_help, container, false);
        unbinder = ButterKnife.bind(this, view);
//        mUrls = new Stack<>();
        initWebView();
        return view;
    }

    private void initWebView() {
        WebSettings webSettings = helpWebContent.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setUseWideViewPort(true);

        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBlockNetworkImage(false);//解决图片加载不出来的问题

        if (Build.VERSION.SDK_INT >= 19) {//4.4 ,小于4.4没有这个方法
            webSettings.setMediaPlaybackRequiresUserGesture(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                ViseLog.d("url = "+url);
                doGotoPage(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                ViseLog.d("onReceivedSslError ");
                super.onReceivedSslError(view, handler, error);
                //webview 忽略证书
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                ViseLog.d("onReceivedError ");
                //6.0以下执行
                /*if (!isWebError) {
                    showErrorPage();
                    isWebError = true;
                }*/
            }

            //处理网页加载失败时
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                ViseLog.d("onReceivedError ");
                //6.0以上执行
               /* if (!isWebError) {
                    showErrorPage();
                    isWebError = true;
                }*/
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ViseLog.d("onPageFinished ");
                /*if (!isWebError) {
                    hideErrorPage();
                }
                isWebError = false;
                if (isRefreshing) {
                    isRefreshing = false;
                    imgNetError.clearAnimation();
                }*/
//                LoadingDialog.dismiss(WebActivity.this);
            }
        };

        helpWebContent.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title.contains("404")) {
                    /*if (!isWebError) {
                        showErrorPage();
                        isWebError = true;
                    }*/
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean value) {
                    ViseLog.d("value= "+value);
                }
            });
        }
        helpWebContent.setWebViewClient(webViewClient);
        helpWebContent.loadUrl(HELP_URL);
    }

    private void showErrorPage() {
//        loadErrorLayout.setVisibility(View.VISIBLE);
//        webviewMain.postInvalidate();
    }

    private void hideErrorPage() {
//        loadErrorLayout.setVisibility(View.GONE);
//        webviewMain.postInvalidate();
    }

    private void doGotoPage(String url) {
//        if (url.startsWith("alpha1e:goBack")) {//
////            this.finish();
//        } else {
            if(helpWebContent != null){
//                mUrls.push(url);
                helpWebContent.loadUrl(url);
            }else {
                //华为平板出现过一次webContent为null, 故作此判断
                ViseLog.e("webContent is null");
            }
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
