package com.aaijee.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aaijee.app.R;

import im.delight.android.webview.AdvancedWebView;


public class MapWebViewActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    private AdvancedWebView web;
    String html="<iframe src=\"https://www.google.com/maps/embed?pb=!1m21!1m12!1m3!1d119850.00083056695!2d72.69178826099656!3d20.162708948220946!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!4m6!3e6!4m0!4m3!3m2!1d20.16273!2d72.76183999999999!5e0!3m2!1sen!2sin!4v1645752619063!5m2!1sen!2sin\" width=\"600\" height=\"450\" style=\"border:0;\" allowfullscreen=\"\" loading=\"lazy\"></iframe>";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_map_web_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_webview);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setTitle("Get Direction");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        web=findViewById(R.id.web);

        web.setListener(this, this);
        web.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                AdvancedWebView newWebView = new AdvancedWebView(MapWebViewActivity.this);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                return true;
            }

        });

        web.setThirdPartyCookiesEnabled(false);
        web.setDesktopMode(false);
        web.setCookiesEnabled(false);
        web.setMixedContentAllowed(false);

        web.getSettings().setJavaScriptEnabled(true);
        web.loadData(html, "text/html", null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MapWebViewActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }


    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }
}