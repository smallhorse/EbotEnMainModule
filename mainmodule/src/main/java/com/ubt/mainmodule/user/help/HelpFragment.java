package com.ubt.mainmodule.user.help;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ubt.mainmodule.MainHttpEntity;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
import com.vise.log.ViseLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/19 15:21
 * @描述:
 */

public class HelpFragment extends SupportFragment {

    @BindView(R2.id.help_web_content)    WebView helpWebContent;
    @BindView(R2.id.img_net_error)    ImageView imgNetError;
    @BindView(R2.id.load_error_layout)    RelativeLayout loadErrorLayout;

    Unbinder unbinder;

    private boolean isWebError = false;
    private boolean isRefreshing = false;
    private Handler mHandler = new Handler();
    private long loadTime = System.currentTimeMillis();

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
        initWebView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(helpWebContent != null) {
            helpWebContent.loadUrl(MainHttpEntity.HELP_FEEDBACK);
        }
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
                super.shouldOverrideUrlLoading(view, url);
                ViseLog.d("url = " + url);

                if(url.contains("email-report")){
                    Intent intent= new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);
                }else {
                    doGotoPage(url);
                }
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
                if (!isWebError) {
                    showErrorPage();
                    isWebError = true;
                }
            }

            //处理网页加载失败时
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                ViseLog.d("onReceivedError ");
                //6.0以上执行
                if (!isWebError) {
                    showErrorPage();
                    isWebError = true;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ViseLog.d("onPageFinished ");

            }
        };

        helpWebContent.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title.contains("404")) {
                    if (!isWebError) {
                        showErrorPage();
                        isWebError = true;
                    }
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                ViseLog.d("newProgress = "+newProgress);
                if(newProgress < 100){
                    return;
                }
                if(System.currentTimeMillis() - loadTime < 300){
                    ViseLog.d("newProgress = "+newProgress+"  重复进度!!");
                    return;
                }
                loadTime = System.currentTimeMillis();
                if (!isWebError) {
                    hideErrorPage();
                }
                isWebError = false;
                if (isRefreshing) {
                    isRefreshing = false;
                    imgNetError.clearAnimation();
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean value) {
                    ViseLog.d("value= " + value);
                }
            });
        }
        helpWebContent.setWebViewClient(webViewClient);
        helpWebContent.loadUrl(MainHttpEntity.HELP_FEEDBACK);
    }

    private void showErrorPage() {
        loadErrorLayout.setVisibility(View.VISIBLE);
        helpWebContent.postInvalidate();
    }

    private void hideErrorPage() {
        loadErrorLayout.setVisibility(View.GONE);
        helpWebContent.postInvalidate();
    }

    private void doGotoPage(String url) {
        if (helpWebContent != null) {
            helpWebContent.loadUrl(url);
        } else {
            //华为平板出现过一次webContent为null, 故作此判断
            ViseLog.e("webContent is null");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R2.id.load_error_layout)
    public void onViewClicked() {
        if(rotate()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    helpWebContent.loadUrl(MainHttpEntity.HELP_FEEDBACK);
                }
            },1000);

        }
    }

    public boolean rotate() {
        if(isRefreshing){
            return false;
        }
        isRefreshing = true;
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        imgNetError.setAnimation(rotateAnimation);
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnimation.setInterpolator(lin);
        rotateAnimation.setDuration(500);
        rotateAnimation.setRepeatCount(-1);//设置重复次数
        rotateAnimation.setFillAfter(false);//动画执行完后是否停留在执行完的状态
        imgNetError.startAnimation(rotateAnimation);
        return true;
    }
}
