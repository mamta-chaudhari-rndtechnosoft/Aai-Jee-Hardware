package com.aaijee.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aaijee.app.R;
import com.aaijee.app.Util.Constants;
import com.aaijee.app.Util.SharedPref;
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

public class SignUpActivity extends AppCompatActivity {

    TextView tvLogin, tvusername, tvmobile, tvpassword, tvotp, tvaddress, tvgst;
    private String mobile;
    Button btnSignup, btnSubmit;
    ProgressBar progress_login;
    LinearLayout otp_layout;
    ScrollView scrollView;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    private FirebaseAuth mAuth;
    CountryCodePicker ccp;
    private String selected_country_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        phoneNumberAuthCallbackListener();

        tvLogin = findViewById(R.id.tvLogin);
        tvusername = findViewById(R.id.tvusername);
        tvmobile = findViewById(R.id.tvmobile);
        tvpassword = findViewById(R.id.tvpassword);
        tvotp = findViewById(R.id.tvotp);
        btnSignup = findViewById(R.id.Signup);
        btnSubmit = findViewById(R.id.btnSubmit);
        progress_login = findViewById(R.id.progress_login);
        otp_layout = findViewById(R.id.otp_layout);
        scrollView = findViewById(R.id.scroll_form);
        ccp = findViewById(R.id.ccp);
        tvgst = findViewById(R.id.edtgst);
        tvaddress = findViewById(R.id.address);

//        GradientDrawable gd = new GradientDrawable();
//        if (SharedPref.getDASHED(SignUpActivity.this).equalsIgnoreCase("1")) {
//            gd.setColor(getResources().getColor(R.color.colorGrey));
//            gd.setCornerRadius(25);
//            gd.setStroke(4, getResources().getColor(R.color.colorGrey1), 12, 16);
//        }else{
//            gd.setColor(getResources().getColor(R.color.colorGrey));
//            gd.setCornerRadius(25);
//            gd.setStroke(4, getResources().getColor(R.color.colorGrey1));
//        }
//        tvusername.setBackground(gd);
//        tvmobile.setBackground(gd);
//        ccp.setBackground(gd);
//        tvpassword.setBackground(gd);
//        tvotp.setBackground(gd);

        SpannableString SpanString = new SpannableString(
                getResources().getString(R.string.already_user));

        ClickableSpan login = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                startActivity(new Intent(SignUpActivity.this, Login.class));

            }
        };
        SpanString.setSpan(login, 22, 28, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorBlack)), 22, 28, 0);
        SpanString.setSpan(new UnderlineSpan(), 22, 28, 0);

        tvLogin.setMovementMethod(LinkMovementMethod.getInstance());
        tvLogin.setText(SpanString, TextView.BufferType.SPANNABLE);
        tvLogin.setSelected(true);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = tvmobile.getText().toString();
                selected_country_code = ccp.getSelectedCountryCodeWithPlus();
                String noWithCode = selected_country_code + mobile;
                startPhoneNumberVerification(noWithCode);
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
                if (progress_login.getVisibility() == View.GONE)
                    progress_login.setVisibility(View.VISIBLE);

                verifyPhoneNumberWithCode(mVerificationId, tvotp.getText().toString().trim());
            }
        });

        ccp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCountryPickerClick(v);
                selected_country_code = ccp.getSelectedCountryCode();
            }
        });

    }

    public void onCountryPickerClick(View view) {
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                selected_country_code = ccp.getSelectedCountryCode();
            }
        });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                SignUpActivity.this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (progress_login.getVisibility() == View.VISIBLE)
                            progress_login.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            tvotp.setText("");
                            Toast.makeText(SignUpActivity.this, "Verified successfully", Toast.LENGTH_LONG).show();

                            signup();

                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(SignUpActivity.this, "Verification failed!Please enter correct otp", Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
    }

    public void phoneNumberAuthCallbackListener() {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified withoutF needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                if (progress_login.getVisibility() == View.VISIBLE)
                    progress_login.setVisibility(View.GONE);
                tvotp.setText(credential.getSmsCode());

                signup();

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                if (progress_login.getVisibility() == View.VISIBLE)
                    progress_login.setVisibility(View.GONE);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    return;

                }
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                if (progress_login.getVisibility() == View.VISIBLE)
                    progress_login.setVisibility(View.GONE);
                Toast.makeText(SignUpActivity.this, "Otp has been sent to your mobile number", Toast.LENGTH_LONG).show();
                // Save verification ID and resending token so we can use them later

                mVerificationId = verificationId;
                scrollView.setVisibility(View.GONE);
                tvLogin.setVisibility(View.GONE);
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
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("USER_REGISTRATION");
                            if (jsonObject1.has("error")) {
                                String error = jsonObject1.getString("error");
                                if (error.equals("false")) {
//                                    String id = jsonObject1.getString("id");
//                                    String name = jsonObject1.getString("name");
//                                    String mobile = jsonObject1.getString("mobile");
//                                    String password = jsonObject1.getString("password");
                                    String gender = jsonObject1.getString("gender");
//                                    String image = jsonObject1.getString("image");
//                                    String wallet = jsonObject1.getString("wallet");
//
//                                    SharedPref.setPreference(SharedPref.USER_NAME,name,SignUpActivity.this);
//                                    SharedPref.setPreference(SharedPref.USER_MOBILE,mobile,SignUpActivity.this);
                                    //                                  SharedPref.setPreference(SharedPref.USER_ID,id,SignUpActivity.this);
                                    SharedPref.setPreference(SharedPref.USER_GENDER, gender, SignUpActivity.this);
//                                    SharedPref.setPreference(SharedPref.USER_PASSWORD,password,SignUpActivity.this);
//                                    SharedPref.setPreference(SharedPref.USER_IMAGE,image,SignUpActivity.this);
//                                    SharedPref.setPreference(SharedPref.WALLET,wallet,SignUpActivity.this);

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
                                    alertDialogBuilder.setMessage(R.string.signup_message);
                                    alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    Intent intent = new Intent(SignUpActivity.this, Login.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();


                                    progress_login.setVisibility(View.GONE);

//                                    Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                    startActivity(intent);
//                                    finish();
                                } else {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
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

                                //  Toast.makeText(SignUpActivity.this, jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();
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
                        progress_login.setVisibility(View.GONE);
                    }
                }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", tvusername.getText().toString());
                params.put("mobile", mobile);
                params.put("password", tvpassword.getText().toString());
//                params.put("address",tvaddress.getText().toString());
//                params.put("gst_no",tvgst.getText().toString());
                params.put("fcm_token", FirebaseInstanceId.getInstance().getToken());
                return params;
            }
        };
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }
}
