package com.aaijee.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aaijee.app.R;
import com.aaijee.app.Util.Constants;

public class AboutusActivity extends AppCompatActivity {

    public Toolbar toolbar;
    private TextView textView_app_name, textView_app_version, textView_app_author, textView_app_contact, textView_app_email, textView_app_website, textView_app_description,
            textView_titleVerson, textView_titleAuthore, textView_titleCompany, textView_titleEmail, textView_titleWebside, textView_titleAbout;

    private ImageView app_logo;
    Button btnDirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_aboutus);

        toolbar = (Toolbar) findViewById(R.id.toolbar_about_us);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setTitle(Html.fromHtml("<b>"+getResources().getString(R.string.contact_us)+"</b>"));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textView_app_name = (TextView) findViewById(R.id.textView_app_name_about_us);
        textView_app_version = (TextView) findViewById(R.id.textView_app_version_about_us);
        textView_app_author = (TextView) findViewById(R.id.textView_app_author_about_us);
        textView_app_contact = (TextView) findViewById(R.id.textView_app_contact_about_us);
        textView_app_email = (TextView) findViewById(R.id.textView_app_email_about_us);
        textView_app_website = (TextView) findViewById(R.id.textView_app_website_about_us);
        textView_app_description = (TextView) findViewById(R.id.textView_app_description_about_us);
        btnDirect=findViewById(R.id.direction);


        textView_titleVerson = (TextView) findViewById(R.id.textView_TitleVersion_about_us);
        textView_titleAuthore = (TextView) findViewById(R.id.textView_TitleAuthore_about_us);
        textView_titleAbout = (TextView) findViewById(R.id.textView_TitleAbout_about_us);
        textView_titleCompany = (TextView) findViewById(R.id.textView_TitleCompany_about_us);
        textView_titleEmail = (TextView) findViewById(R.id.textView_TitleEmail_about_us);
        textView_titleWebside = (TextView) findViewById(R.id.textView_TitleWebsite_about_us);
        textView_titleAbout = (TextView) findViewById(R.id.textView_TitleContact_about_us);

        app_logo = (ImageView) findViewById(R.id.app_logo_about_us);

        textView_app_name.setText(Constants.aboutUs.getApp_name());

        textView_app_version.setText(Constants.aboutUs.getApp_version());
        textView_app_author.setText(Constants.aboutUs.getApp_author());
        textView_app_contact.setText(Constants.aboutUs.getApp_contact());
        textView_app_email.setText(Constants.aboutUs.getApp_email());
        textView_app_website.setText(Constants.aboutUs.getApp_website());
        textView_app_description.setText(Html.fromHtml(Constants.aboutUs.getApp_description()));


        btnDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri= Uri.parse("https://goo.gl/maps/daoU4GTmwUhVsXKQA");
                Intent i= new Intent(Intent.ACTION_VIEW,uri);
                i.setPackage("com.google.android.apps.maps");
                startActivity(i);

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
