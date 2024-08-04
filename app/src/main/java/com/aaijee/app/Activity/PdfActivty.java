package com.aaijee.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aaijee.app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import im.delight.android.webview.AdvancedWebView;


public class PdfActivty extends AppCompatActivity implements AdvancedWebView.Listener  {
    private TextView textView;

    private AdvancedWebView mWebView;
    Button btn;
    FloatingActionButton fab;
    String pdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        Intent i=getIntent();
        pdf= i.getStringExtra("pdf");
        Toast.makeText(PdfActivty.this, "2= "+pdf, Toast.LENGTH_SHORT).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_about_us);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setTitle(Html.fromHtml("<b>" + "PDF" + "</b>"));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        if (savedInstanceState == null) {
//            Bundle extras = getIntent().getExtras();
//            if (extras == null) {
//                order = 0;
//            } else {
//                order = extras.getInt("order_id");
//            }
//        } else {
//            order = (Integer) savedInstanceState.getSerializable("order_id");
//        }

        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        mWebView.setListener(this, this);
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                AdvancedWebView newWebView = new AdvancedWebView(PdfActivty.this);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;

            }

        });

        // disable third-party cookies only
        mWebView.setThirdPartyCookiesEnabled(false);
        mWebView.setDesktopMode(false);
        // or disable cookies in general
        mWebView.setCookiesEnabled(true);
        mWebView.setMixedContentAllowed(true);
        mWebView.loadUrl("https://aaijeehardware.com/app/images/pdf/netaaijee11122021cdr(1).pdf");


    }





    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(PdfActivty.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) { }

    @Override
    public void onPageFinished(String url) {
        // fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) { }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) { }

    @Override
    public void onExternalPageRequest(String url) { }

    private void createWebPrintJob(WebView webView) {

        //create object of print manager in your device
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        //create object of print adapter
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        //provide name to your newly generated pdf file
        String jobName = getString(R.string.app_name) + " Print Test";

        //open print dialog
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }

    //perform click pdf creation operation on click of print button click
    public void printPDF(View view) {
        createWebPrintJob(mWebView);
    }

}
