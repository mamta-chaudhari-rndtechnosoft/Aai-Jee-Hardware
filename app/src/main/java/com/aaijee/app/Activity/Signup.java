package com.aaijee.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aaijee.app.R;
import com.aaijee.app.Util.Constants;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hbb20.CountryCodePicker;
 

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Signup extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
    TextView tvLogin,tvusername,tvmobile, tvemail,tvotp;
    private String name,mobile,password;
    Button btnSignup,btnSubmit;
    ProgressBar progress_login;
    LinearLayout otp_layout;
    LinearLayout scrollView,ll,ll1;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    private FirebaseAuth mAuth;
    CountryCodePicker ccp;
    private String selected_country_code;
    SwipeRefreshLayout swipe_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.new_signup);

        mAuth = FirebaseAuth.getInstance();
        phoneNumberAuthCallbackListener();

        tvLogin = (TextView) findViewById(R.id.tvLogin);
        tvusername = (TextView) findViewById(R.id.tvusername);
        tvmobile = (TextView) findViewById(R.id.tvmobile);
        tvemail = (TextView) findViewById(R.id.tvemail);
        tvotp = (TextView) findViewById(R.id.tvotp);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        progress_login = (ProgressBar) findViewById(R.id.progress_login);
        otp_layout = findViewById(R.id.otp_layout);
     //   scrollView = findViewById(R.id.scroll_form);
        ccp = findViewById(R.id.ccp);
        ll=findViewById(R.id.ll);
        ll1=findViewById(R.id.ll1);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        swipe_refresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.colorWhite));
        int col = getResources().getColor(R.color.colorAccent);
        swipe_refresh.setColorSchemeColors(col,col,col);
        swipe_refresh.setOnRefreshListener(this);

        /*GradientDrawable gd = new GradientDrawable();
        if (SharedPref.getDASHED(Signup.this).equalsIgnoreCase("1")) {
            gd.setColor(getResources().getColor(R.color.colorGrey));
            gd.setCornerRadius(25);
//        gd.setStroke(2, Color.RED);
            gd.setStroke(4, getResources().getColor(R.color.colorGrey1), 12, 16);
        }else{
            gd.setColor(getResources().getColor(R.color.colorGrey));
            gd.setCornerRadius(25);
            gd.setStroke(4, getResources().getColor(R.color.colorGrey1));
        }
        tvusername.setBackground(gd);
        tvmobile.setBackground(gd);
        ccp.setBackground(gd);
        tvemail.setBackground(gd);
        tvotp.setBackground(gd);*/

        SpannableString SpanString = new SpannableString(
                getResources().getString(R.string.already_user));

        ClickableSpan login = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                startActivity(new Intent(Signup.this, Login.class));

            }
        };
        SpanString.setSpan(login, 22, 28, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 22, 28, 0);
        SpanString.setSpan(new UnderlineSpan(), 22, 28, 0);

        tvLogin.setMovementMethod(LinkMovementMethod.getInstance());
        tvLogin.setText(SpanString, TextView.BufferType.SPANNABLE);
        tvLogin.setSelected(true);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean flag = checkPhone();
                if (flag) {
                    verifyMobile();
                }
                btnSignup.setText("Please Wait...");
                btnSignup.setEnabled(false);

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvotp.setError(null);
                if (tvotp.length() == 0) {
                    tvotp.setError(getString(R.string.please_enter_otp));
                    return;
                }
                if(progress_login.getVisibility()==View.GONE)
                    progress_login.setVisibility(View.VISIBLE);

                verifyPhoneNumberWithCode(mVerificationId, tvotp.getText().toString().trim());
            }
        });

        ccp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCountryPickerClick(v);
                selected_country_code=ccp.getSelectedCountryCode();
            }
        });

    }

    public Boolean checkPhone()
    {

        mobile = tvmobile.getText().toString();
        if (mobile.length() == 0)
        {
            tvmobile.setError(getString(R.string.error_field_empty));
            return false;
        }

        if (mobile.length()!=10) {
            tvmobile.setError(getString(R.string.wrong_format));
            return false;
        }
        tvmobile.setError(null);

        return true;
    }

    private void verifyMobile()
    {
        progress_login.setVisibility(View.VISIBLE);
        final RequestQueue requestQueue = Volley.newRequestQueue(Signup.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.verify_mobile_no+"="+tvmobile.getText().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject object = jsonObject.getJSONObject("VERIFY_MOBILE_NUMBER");
                    String error = object.getString("error");
                    String message = object.getString("message");

                    if (error.equalsIgnoreCase("false")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Signup.this);
                        alertDialogBuilder.setMessage(message);
                        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1)
                                    {
                                        progress_login.setVisibility(View.GONE);
                                        btnSignup.setEnabled(true);
                                        btnSignup.setText("Sign Up");
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                    else
                        {

                        progress_login.setVisibility(View.VISIBLE);


                        /*mobile = tvmobile.getText().toString();
                        selected_country_code = ccp.getSelectedCountryCodeWithPlus();
                        String noWithCode = selected_country_code + mobile;
                        startPhoneNumberVerification(noWithCode);*/
                        mobile = tvmobile.getText().toString();
                        selected_country_code = ccp.getSelectedCountryCodeWithPlus();
                        String noWithCode = selected_country_code+mobile;
                        startPhoneNumberVerification(noWithCode);

                        }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    progress_login.setVisibility(View.GONE);
                    btnSignup.setEnabled(true);
                    btnSignup.setText("Sign Up");
                    Toast.makeText(Signup.this, "Invalid mobile or password !", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestQueue.stop();
                progress_login.setVisibility(View.GONE);
                btnSignup.setEnabled(true);
                btnSignup.setText("Sign Up");
                Toast.makeText(Signup.this, "Invalid mobile or password", Toast.LENGTH_SHORT).show();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void onCountryPickerClick(View view) {
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                selected_country_code = ccp.getSelectedCountryCode();
                Log.d("Country Code", selected_country_code);
            }
        });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Signup.this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]



    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        progress_login.setVisibility(View.GONE);
        btnSignup.setEnabled(true);
        btnSignup.setText("Sign Up");

        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]

        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(progress_login.getVisibility() == View.VISIBLE)
                            progress_login.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");

                            tvotp.setText("");
                            progress_login.setVisibility(View.GONE);
                            Toast.makeText(Signup.this,"Verified successfully",Toast.LENGTH_LONG).show();

                            signup();
                        } else {
                            // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(Signup.this,"Verification failed!Please enter correct otp",Toast.LENGTH_LONG).show();
                                progress_login.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    public void phoneNumberAuthCallbackListener()
    {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d("TAG", "onVerificationCompleted:" + credential);
                if(progress_login.getVisibility() == View.VISIBLE)
                    progress_login.setVisibility(View.GONE);
                tvotp.setText(credential.getSmsCode());

                signup();

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("TAG", "onVerificationFailed", e);
                if(progress_login.getVisibility() == View.VISIBLE)
                    progress_login.setVisibility(View.GONE);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    progress_login.setVisibility(View.GONE);
                    Toast.makeText(Signup.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    return;
                }
                else if (e instanceof FirebaseTooManyRequestsException) {
                    progress_login.setVisibility(View.GONE);
                    Toast.makeText(Signup.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    return;

                }
                progress_login.setVisibility(View.GONE);
                Toast.makeText(Signup.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                if(progress_login.getVisibility() == View.VISIBLE)
                    progress_login.setVisibility(View.GONE);
                Log.d("TAG", "onCodeSent:" + verificationId);
                Toast.makeText(Signup.this,"Otp has been sent to your mobile number",Toast.LENGTH_LONG).show();
                // Save verification ID and resending token so we can use them later

                mVerificationId = verificationId;
           //     scrollView.setVisibility(View.GONE);
                tvLogin.setVisibility(View.GONE);
                ll.setVisibility(View.GONE);
                ll1.setVisibility(View.GONE);
                otp_layout.setVisibility(View.VISIBLE);
            }
        };
    }

    private void signup() {
        progress_login.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.sign_up,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Response
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONObject jsonObject1=jsonObject.getJSONObject("USER_REGISTRATION");
                            if (jsonObject1.has("error")){
                                String error = jsonObject1.getString("error");
                                if (error.equals("false")) {
                                    String id = jsonObject1.getString("id");
                                    String name = jsonObject1.getString("name");
                                    String mobile = jsonObject1.getString("mobile");
                                    String email = jsonObject1.getString("email");

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Signup.this);
                                    alertDialogBuilder.setMessage(R.string.signup_message);
                                    alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    Intent intent = new Intent(Signup.this,Login.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();

                                    /*SharedPref.setPreference(SharedPref.USER_NAME,name,Signup.this);
                                    SharedPref.setPreference(SharedPref.USER_MOBILE,mobile,Signup.this);
                                    SharedPref.setPreference(SharedPref.USER_ID,id,Signup.this);
                                    SharedPref.setPreference(SharedPref.USER_EMAIL,email,Signup.this);*/

                                    progress_login.setVisibility(View.GONE);


                                }else {
                                    //Toast.makeText(Signup.this, jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Signup.this);
                                    alertDialogBuilder.setMessage(jsonObject1.getString("message"));
                                    alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {

                                                }
                                            });

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progress_login.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Error
                        Log.d("SignupError->>",error.toString());
                        progress_login.setVisibility(View.GONE);
                    }
                }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", tvusername.getText().toString());
                params.put("mobile", mobile);
                params.put("email", tvemail.getText().toString());
                params.put("fcm_token", FirebaseInstanceId.getInstance().getToken());
                return params;
            }
        };
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(Signup.this);
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    @Override
    public void onRefresh() {

        tvmobile.setText("");
        tvusername.setText("");
        tvemail.setText("");
        btnSignup.setEnabled(true);
        swipe_refresh.setRefreshing(false);
    }
}
