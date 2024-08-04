package com.aaijee.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aaijee.app.R;
import com.aaijee.app.Util.Constants;
import com.aaijee.app.Util.Method;
import com.aaijee.app.Util.SharedPref;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_splash);

        getConfig();

        if (Method.haveNetworkConnection(SplashActivity.this)) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    SharedPreferences preferences = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
                    if (SharedPref.getMobileNumber(SplashActivity.this) != null & SharedPref.getMobileNumber(SplashActivity.this).equals("")) {
                        Intent i = new Intent(SplashActivity.this, Login.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    /*Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();*/

                }
            }, SPLASH_TIME_OUT);
        } else {
            Toast.makeText(SplashActivity.this, getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(getResources().getString(R.string.internet_connection_message));
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.exit),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
    }

    private void getConfig() {
        final RequestQueue requestQueue = Volley.newRequestQueue(SplashActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.config, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("config");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String currency = jsonObject1.getString("currency");
                        String dashed = jsonObject1.getString("dashed");
                        String app_status = jsonObject1.getString("app_status");

                        SharedPref.setPreference(SharedPref.CURRENCY, currency, SplashActivity.this);
                        SharedPref.setPreference(SharedPref.DASHED, dashed, SplashActivity.this);
                        SharedPref.setPreference(SharedPref.APP_STATUS, app_status, SplashActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestQueue.stop();
            }
        });
        requestQueue.add(stringRequest);
    }
}
