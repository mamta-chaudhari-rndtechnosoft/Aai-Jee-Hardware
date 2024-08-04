package com.aaijee.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Html;
import android.view.WindowManager;
import android.widget.TextView;

import com.aaijee.app.R;
import com.aaijee.app.Util.Constants;

public class PrivacyActivity extends AppCompatActivity {

    public Toolbar toolbar;
    private TextView privacy_policy_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_privacy);

        toolbar = (Toolbar) findViewById(R.id.toolbar_privacy_policy);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setTitle(Html.fromHtml("<b>"+getResources().getString(R.string.privacy_policy)+"</b>"));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        privacy_policy_textview=(TextView)findViewById(R.id.textview_privacy_policy);
        privacy_policy_textview.setText(Html.fromHtml(Constants.aboutUs.getApp_privacy_policy()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
