package com.aaijee.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aaijee.app.R;
import com.aaijee.app.Util.Constants;
import com.aaijee.app.Util.SharedPref;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
 {
    EditText tvmobile,tvOtp;
    TextView btnSignup,tvCustomerCatalogue;
    Button btnLogin,btnSubmit;
    private String mobile="";
    private String password="";
    ProgressBar progress_login;
    CountryCodePicker ccp;
    LinearLayout main_layout, otp_layout,ll;
    private String selected_country_code;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    CardView cd;
    private FirebaseAuth mAuth;
    SwipeRefreshLayout swipe_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.new_login);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        swipe_refresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.colorWhite));
        int col = getResources().getColor(R.color.colorAccent);
        swipe_refresh.setColorSchemeColors(col,col,col);
        swipe_refresh.setOnRefreshListener(this);

        FirebaseApp.initializeApp(Login.this);
        mAuth = FirebaseAuth.getInstance();
        phoneNumberAuthCallbackListener();

        initView();
    }

    private void initView() {
        tvmobile = findViewById(R.id.tvusername);
        tvOtp = findViewById(R.id.tvOtp);
        tvCustomerCatalogue = findViewById(R.id.tvCustomerCatalogue);
        btnSignup = findViewById(R.id.btnSignup);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnLogin = findViewById(R.id.btnLogin);
        progress_login = findViewById(R.id.progress_login);
        main_layout = findViewById(R.id.main_layout);
        otp_layout = findViewById(R.id.otp_layout);
        progress_login.setVisibility(View.GONE);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        cd=findViewById(R.id.card);
        ll=findViewById(R.id.ll);
        main_layout.setVisibility(View.VISIBLE);
        otp_layout.setVisibility(View.GONE);

        SpannableString SpanString = new SpannableString(
                getResources().getString(R.string.dont_have_account));

        ClickableSpan signup = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent mIntent = new Intent(Login.this, Signup.class);
                mIntent.putExtra("signup", true);
                startActivity(mIntent);
            }
        };

        SpanString.setSpan(signup, 23, 30, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorWhite)), 23, 30, 0);
        SpanString.setSpan(new UnderlineSpan(), 23, 30, 0);

        btnSignup.setMovementMethod(LinkMovementMethod.getInstance());
        btnSignup.setText(SpanString, TextView.BufferType.SPANNABLE);
        btnSignup.setSelected(true);

        ccp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCountryPickerClick(v);
                selected_country_code=ccp.getSelectedCountryCode();
                GradientDrawable gd = new GradientDrawable();
                if (SharedPref.getDASHED(Login.this).equalsIgnoreCase("1")) {
                    gd.setColor(getResources().getColor(R.color.colorGrey));
                    gd.setCornerRadius(25);
                    gd.setStroke(4, getResources().getColor(R.color.colorGrey1), 12, 16);
                }else
                {
                    gd.setColor(getResources().getColor(R.color.colorGrey));
                    gd.setCornerRadius(25);
                    gd.setStroke(4, getResources().getColor(R.color.colorGrey1));
                }
                ccp.setBackground(gd);
            }
        });

        clickevents();

        tvCustomerCatalogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, CustomerCatalogueActivity.class));
            }
        });
    }

    public void onCountryPickerClick(View view) {
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                /*GradientDrawable gd = new GradientDrawable();
                if (SharedPref.getDASHED(Login.this).equalsIgnoreCase("1")) {
                    gd.setColor(getResources().getColor(R.color.colorGrey));
                    gd.setCornerRadius(25);
                    gd.setStroke(4, getResources().getColor(R.color.colorGrey1), 12, 16);
                }else{
                    gd.setColor(getResources().getColor(R.color.colorGrey));
                    gd.setCornerRadius(25);
                    gd.setStroke(4, getResources().getColor(R.color.colorGrey1));
                }
                ccp.setBackground(gd);*/
                selected_country_code = ccp.getSelectedCountryCode();
                Log.d("Country Code", selected_country_code);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void clickevents() {

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Signup.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                progress_login.setVisibility(View.VISIBLE);
                boolean flag = checkPhone();
                if (flag)
                {
                    verifyMobile();
                }
                btnLogin.setText("Please Wait...");
                btnLogin.setEnabled(false);

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_login.setVisibility(View.VISIBLE);
                tvOtp.setError(null);
                if (tvOtp.length() == 0) {
                    tvOtp.setError(getString(R.string.please_enter_otp));
                    return;
                }
                if(progress_login.getVisibility()==View.GONE)
                    progress_login.setVisibility(View.VISIBLE);

                verifyPhoneNumberWithCode(mVerificationId, tvOtp.getText().toString().trim());
            }
        });
    }

    private void verifyMobile() {
        progress_login.setVisibility(View.VISIBLE);
        final RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.verify_mobile_no+"="+tvmobile.getText().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject object = jsonObject.getJSONObject("VERIFY_MOBILE_NUMBER");
                    String error = object.getString("error");
                    String message = object.getString("message");

                    if (error.equalsIgnoreCase("false")) {
                        progress_login.setVisibility(View.VISIBLE);
                        mobile = tvmobile.getText().toString();
                        selected_country_code = ccp.getSelectedCountryCodeWithPlus();
                        String noWithCode = selected_country_code + mobile;
                        startPhoneNumberVerification(noWithCode);
                        progress_login.setVisibility(View.GONE);
                    }
                    else
                    {
                        //Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login.this);
                        alertDialogBuilder.setMessage(message);
                        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1)
                                    {
                                        //Toast.makeText(Login.this, "hello", Toast.LENGTH_SHORT).show();
                                        progress_login.setVisibility(View.GONE);
                                        btnLogin.setEnabled(true);
                                        btnLogin.setText("Submit");

                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }


                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    progress_login.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Submit");
                    Toast.makeText(Login.this, "Invalid mobile or password", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestQueue.stop();
                progress_login.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                btnLogin.setText("Submit");
                Toast.makeText(Login.this, "Invalid mobile or password", Toast.LENGTH_SHORT).show();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Login.this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        progress_login.setVisibility(View.GONE);
        btnLogin.setEnabled(true);
        btnLogin.setText("Submit");
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            tvOtp.setText("");
                            Toast.makeText(Login.this,"Verified successfully",Toast.LENGTH_LONG).show();
                            login();

                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(Login.this,"Verification failed!Please enter correct otp",Toast.LENGTH_LONG).show();
                                progress_login.setVisibility(View.GONE);
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
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d("TAG", "onVerificationCompleted:" + credential);
                tvOtp.setText(credential.getSmsCode());

                login();
                if(progress_login.getVisibility() == View.VISIBLE)
                    progress_login.setVisibility(View.GONE);
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
                    Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    return;
                }
                else if (e instanceof FirebaseTooManyRequestsException) {
                    progress_login.setVisibility(View.GONE);
                    Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    return;

                }
                progress_login.setVisibility(View.GONE);
                Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                Log.d("TAG", "onCodeSent:" + verificationId);
                Toast.makeText(Login.this,"Otp has been sent to your mobile number",Toast.LENGTH_LONG).show();
                // Save verification ID and resending token so we can use them later

                mVerificationId = verificationId;
               // main_layout.setVisibility(View.GONE);
                otp_layout.setVisibility(View.VISIBLE);
                cd.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
              //  btnSignup.setVisibility(View.GONE);

                if(progress_login.getVisibility() == View.VISIBLE)
                    progress_login.setVisibility(View.GONE);
            }
        };
    }

    public Boolean checkPhone() {

        mobile = tvmobile.getText().toString();
        if (mobile.length() == 0) {
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

    private void
    login() {
        progress_login.setVisibility(View.VISIBLE);
        final RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.login_otp+"&mobile="+tvmobile.getText().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject object = jsonObject.getJSONObject("LOGGED_IN_USER");
                    String id = object.getString("id");
                    String name = object.getString("name");
                    String mobile = object.getString("mobile");
                    String email = object.getString("email");
                    String gender = object.getString("gender");
                    String image = object.getString("image");

                    SharedPref.setPreference(SharedPref.USER_NAME,name,Login.this);
                    SharedPref.setPreference(SharedPref.USER_MOBILE,mobile,Login.this);
                    SharedPref.setPreference(SharedPref.USER_ID,id,Login.this);
                    SharedPref.setPreference(SharedPref.USER_GENDER,gender,Login.this);
                    SharedPref.setPreference(SharedPref.USER_EMAIL,email,Login.this);
                    SharedPref.setPreference(SharedPref.USER_IMAGE,image,Login.this);

                    progress_login.setVisibility(View.GONE);

                    Intent intent = new Intent(Login.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                    progress_login.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Invalid mobile or password", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestQueue.stop();
                progress_login.setVisibility(View.GONE);
                Toast.makeText(Login.this, "Invalid mobile or password", Toast.LENGTH_SHORT).show();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    @Override
    public void onRefresh() {

        tvmobile.setText("");
        btnLogin.setEnabled(true);
        swipe_refresh.setRefreshing(false);

    }
}
