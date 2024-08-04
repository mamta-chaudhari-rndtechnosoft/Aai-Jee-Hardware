package com.aaijee.app.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aaijee.app.Adapter.CartAdapter;
import com.aaijee.app.Model.Address;
import com.aaijee.app.Model.Cart;
import com.aaijee.app.Model.Toppings;
import com.aaijee.app.R;
import com.aaijee.app.Util.Constants;
import com.aaijee.app.Util.Method;
import com.aaijee.app.Util.SharedPref;
import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    RecyclerView recycler_cart;
    CartAdapter adapter;
    ProgressBar progress_cart;
    String total_price,gst;
    private String address="",address_id="";
    ArrayList<Cart> carts;
    Toolbar toolbar;
    RelativeLayout main_rel,address_lay, address_bill;
    TextView tvTotal,tvDelivery,btnCheckout,btnShopping,txtDiscAmt;
    private long mLastClickTime=0;
    ImageView edit_address;
    private TextView del_label,deliveraddress,txtcoupon,tvSubPrice,txtWallet,txtBalance,txtGst;
    private Address addressModel;
    private LinearLayout linearcoupon,linearcode,discountlayout,linearWallet,tvNodata;
    private String title;
    private String message;
    private String api_amount;
    private String coupon_code;
    private String discount="";
    private String pay_amount;
    double cwall=0;
    ImageView img_cancel;
    private String comment="";
    CheckBox checkWallet,checkAdd;
    private double wallet=0.0;
    double totalamount=0;
    private String delivery_charges;
    LottieAnimationView loader;
    View view2,view3,view1,totalView,viewBill,viewChoice;
    static boolean flag=false;
    private String area_delivery;

    private RelativeLayout relativeLayout;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_cart);

        toolbar = findViewById(R.id.toolbar_cart);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setTitle(Html.fromHtml("<b>"+getResources().getString(R.string.cart)+"</b>"));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        relativeLayout = findViewById(R.id.click);

        getWallet();

        carts=new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            clearCouponData();
        } else {
            title= extras.getString("title");
            message = extras.getString("message");
            api_amount = extras.getString("amount");
            pay_amount = extras.getString("pay_amount");
            coupon_code = extras.getString("coupon_code");
            discount = extras.getString("discount");
            cwall = extras.getInt("wamt");
            showCustomDialog(title,message);
        }

        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
         view1 = findViewById(R.id.view_address);
        totalView = findViewById(R.id.total_view);
        //viewBill=findViewById(R.id.billing_view);
        //viewChoice=findViewById(R.id.choice_view);

        if (SharedPref.getDASHED(CartActivity.this).equalsIgnoreCase("1")) {
            view1.setBackground(getResources().getDrawable(R.drawable.dashed_line));
            view2.setBackground(getResources().getDrawable(R.drawable.dashed_line));
            view3.setBackground(getResources().getDrawable(R.drawable.dashed_line));
            //viewBill.setBackground(getResources().getDrawable(R.drawable.dashed_line));
            totalView.setBackground(getResources().getDrawable(R.drawable.dashed_line));
            //viewChoice.setBackground(getResources().getDrawable(R.drawable.dashed_line));
        }else{
           view1.setBackground(getResources().getDrawable(R.drawable.stroke_line));
            view2.setBackground(getResources().getDrawable(R.drawable.stroke_line));
            view3.setBackground(getResources().getDrawable(R.drawable.stroke_line));
           // viewBill.setBackground(getResources().getDrawable(R.drawable.stroke_line));
            totalView.setBackground(getResources().getDrawable(R.drawable.stroke_line));
            //viewChoice.setBackground(getResources().getDrawable(R.drawable.stroke_line));
        }

        linearcode = findViewById(R.id.linearcode);
        linearWallet = findViewById(R.id.wallet);
        linearWallet.setVisibility(View.GONE);
        checkWallet= findViewById(R.id.checkWallet);
        //checkAdd=findViewById(R.id.checkAddress);
        txtWallet= findViewById(R.id.tvWallet);
        txtBalance= findViewById(R.id.txtBalance);
        address_lay = findViewById(R.id.bottom_sheet);
        //address_bill=findViewById(R.id.rv_bill);
        linearcode.setVisibility(View.GONE);
        img_cancel = (ImageView) findViewById(R.id.img_cancel);
        linearcoupon = findViewById(R.id.linearcoupon);
        discountlayout = findViewById(R.id.discount);
        txtcoupon = findViewById(R.id.txtcoupon);
        tvDelivery = findViewById(R.id.tvDelivery);
        txtDiscAmt = findViewById(R.id.txtDiscAmt);
        tvTotal = findViewById(R.id.tvTotal);
        tvSubPrice = findViewById(R.id.tvSubPrice);
        main_rel = findViewById(R.id.main_rel);
        tvNodata = findViewById(R.id.tvNodata);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnShopping = findViewById(R.id.btnShopping);
        recycler_cart = findViewById(R.id.recycler_cart);
        progress_cart = findViewById(R.id.progress_cart);
        txtGst=findViewById(R.id.txtGst);

        loader = findViewById(R.id.loader);

        recycler_cart.setLayoutManager(new LinearLayoutManager(CartActivity.this));

        edit_address = findViewById(R.id.edit_address);
        del_label = findViewById(R.id.del_label);
        deliveraddress = findViewById(R.id.deliveraddress);

        String address = SharedPref.getPreference(Constants.ADDRESS,"",CartActivity.this);
        if (address!=null && !address.equals(""))
            deliveraddress.setText(address);

        edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CartActivity.this,AddressActivity.class);
                startActivityForResult(intent,Constants.CHOOSE_ADDRESS_CODE);
            }
        });

        address_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this,AddressActivity.class);
                startActivityForResult(intent,Constants.CHOOSE_ADDRESS_CODE);
            }
        });

        btnShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // place order
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                btnCheckout.setEnabled(false);
                progress_cart.setVisibility(View.VISIBLE);
                if (address_id != null && !address_id.equalsIgnoreCase("")) {
                    String tot_val = tvTotal.getText().toString().trim();
                    String[] splited = tot_val.split(Method.escapeSpecialRegexChars(SharedPref.getCurrency(CartActivity.this)));
                    try {
                        tot_val = splited[1];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(CartActivity.this, PayActivity.class);
                    intent.putExtra("wallet_amount", String.valueOf(cwall));
                    intent.putExtra("comment", comment);
                    intent.putExtra("discount", discount);
                    intent.putExtra("delivery", delivery_charges);
                    intent.putExtra("sub_price", total_price);
                    intent.putExtra("total_price", tot_val);
                    intent.putExtra("address_id", address_id);
                    intent.putExtra("gst",gst);
                    //Toast.makeText(CartActivity.this, ""+gst, Toast.LENGTH_SHORT).show();
                    startActivity(intent);


                } else {
                    Toast.makeText(CartActivity.this, "Please Select Address", Toast.LENGTH_SHORT).show();
                    btnCheckout.setEnabled(true);
                    progress_cart.setVisibility(View.GONE);
                }
            }
        });
        if (discount!=null && !discount.equals("")) {
            txtDiscAmt.setText("- " + SharedPref.getCurrency(CartActivity.this)+ discount);
            txtDiscAmt.setTextColor(getResources().getColor(R.color.colorRed));
            tvTotal.setText(SharedPref.getCurrency(CartActivity.this)+ pay_amount);
            discountlayout.setVisibility(View.VISIBLE);
        }else {
            discountlayout.setVisibility(View.GONE);
        }

        if (coupon_code!=null && !coupon_code.equals("") && discount!=null && !discount.equals("")){
            linearcoupon.setVisibility(View.GONE);
            linearcode.setVisibility(View.VISIBLE);
            txtcoupon.setText(coupon_code);
        }

        img_cancel.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                linearcoupon.setVisibility(View.VISIBLE);
                linearcode.setVisibility(View.GONE);
                discountlayout.setVisibility(View.GONE);
                clearCouponData();
                if (checkWallet.isChecked())
                    tvTotal.setText(SharedPref.getCurrency(CartActivity.this) + (Integer.parseInt(total_price)-cwall));
                else
                    tvTotal.setText(SharedPref.getCurrency(CartActivity.this) + (Double.parseDouble(total_price)));
           //+Double.parseDouble(delivery_charges)
            }
        });

//        checkAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(checkAdd.isChecked())
//                {
//                    address_lay.setVisibility(View.GONE);
//                    viewBill.setVisibility(View.INVISIBLE);
//                }
//                else
//                {
//                    address_lay.setVisibility(View.VISIBLE);
//                    viewBill.setVisibility(View.VISIBLE);
//                }
//            }
//        });




//        address_bill.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(CartActivity.this,AddressActivity.class);
//                startActivityForResult(intent,Constants.CHOOSE_ADDRESS_CODE);
//
//            }
//        });


        linearcoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this,CouponActivity.class)
                        .putExtra("amount",tvTotal.getText().toString())
                        .putExtra("wamt",cwall));
            }
        });

        checkWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkWallet.isChecked()) {
                    checkwallet();
                }else if (!checkWallet.isChecked()){
                    uncheckwallet();
                }
            }
        });
    }

    private void getWallet()
    {
        final String userid= SharedPref.getUserId(CartActivity.this);
        final RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.view_profile+userid, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                String res = new String(response);
                try {
                    JSONObject jsonObject = new JSONObject(res);

                    JSONArray jsonArray = jsonObject.getJSONArray("USER_PROFILE");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        wallet = Double.parseDouble(object.getString("wallet"));
                        txtBalance.setText(SharedPref.getCurrency(CartActivity.this)+wallet);
                        SharedPref.setPreference(SharedPref.WALLET, String.valueOf(wallet),CartActivity.this);
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

    @SuppressLint("SetTextI18n")
    private void checkwallet()
    {
        Constants.WALLET_FLAG = true;
        cwall = wallet;
        String payamt = null;

        String[] splited = tvTotal.getText().toString().split(Method.escapeSpecialRegexChars(SharedPref.getCurrency(CartActivity.this)));
        try {
            payamt= splited[1];
        } catch (Exception e) {
            e.printStackTrace();

        }

        if (Double.valueOf(payamt) >= cwall && cwall!=0)
        {

            totalamount = Double.valueOf(payamt) - cwall;
            DecimalFormat df = new DecimalFormat("#.##");
            totalamount= Double.parseDouble(df.format(Double.parseDouble(String.valueOf(totalamount))));
            tvTotal.setText(SharedPref.getCurrency(CartActivity.this)+ totalamount);
            linearWallet.setVisibility(View.VISIBLE);
            txtWallet.setText("-"+SharedPref.getCurrency(CartActivity.this)+String.valueOf(cwall));
            txtWallet.setTextColor(getResources().getColor(R.color.colorRed));
        }else if (Double.valueOf(payamt)<=cwall && cwall!=0) {
            tvTotal.setText(SharedPref.getCurrency(CartActivity.this) + " 0");
            cwall = Double.parseDouble(payamt);
            linearWallet.setVisibility(View.VISIBLE);
            txtWallet.setText("-"+SharedPref.getCurrency(CartActivity.this)+String.valueOf(cwall));
            txtWallet.setTextColor(getResources().getColor(R.color.colorRed));
        }
    }

    private void uncheckwallet()
    {
        Constants.WALLET_FLAG=false;
        cwall=0;
        if (pay_amount!=null)
            tvTotal.setText(SharedPref.getCurrency(CartActivity.this)+pay_amount);
        else
            tvTotal.setText(SharedPref.getCurrency(CartActivity.this) + (Double.parseDouble(total_price) ));

        //+Double.parseDouble(delivery_charges)
        linearWallet.setVisibility(View.GONE);
        txtWallet.setText("-"+SharedPref.getCurrency(CartActivity.this)+String.valueOf(wallet));
        txtWallet.setTextColor(getResources().getColor(R.color.colorRed));
    }

    public void showCustomDialog(String title,String message)
    {
        hideKeyboard(CartActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.my_dialog, viewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CartActivity.this);
        builder.setView(dialogView);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

        TextView title_dialog = dialogView.findViewById(R.id.title_dialog);
        TextView msg_dialog = dialogView.findViewById(R.id.message_dialog);
        Button buttonOk = dialogView.findViewById(R.id.buttonOk);
        title_dialog.setText(title);
        msg_dialog.setText(message);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void clearCouponData() {
        title= null;
        message = null;
        api_amount = null;
        pay_amount = null;
        discount="";
        coupon_code=null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_delete, menu);


        MenuItem item = menu.findItem(R.id.clear);

        if (flag) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;

            case R.id.clear:
                showClearDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showClearDialog()

    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        builder.setMessage(getString(R.string.clear_all_order));
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deletecart();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deletecart() {

        progress_cart.setVisibility(View.VISIBLE);
        final RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.delete_cart+"&type=All&user_id="+SharedPref.getUserId(CartActivity.this), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                onResume();
                flag = false;
                invalidateOptionsMenu();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestQueue.stop();
                progress_cart.setVisibility(View.GONE);
            }
        });
        requestQueue.add(stringRequest);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCartList();
    }

    public void getDiscount(final String coupon_code, final String amount) {

        final RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.get_discount, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("GET_DISCOUNT");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        String error = object.getString("error");
                        title = object.getString("title");
                        message = object.getString("message");
                        api_amount = object.getString("amount");
                        pay_amount = object.getString("pay_amount");
                        discount = object.getString("discount");
                        //showCustomDialog(title,message);
                        if (error.equalsIgnoreCase("false")){
                            txtDiscAmt.setText("- " + SharedPref.getCurrency(CartActivity.this) + discount);
                            tvTotal.setText(SharedPref.getCurrency(CartActivity.this)+ Double.parseDouble(pay_amount));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestQueue.stop();
                progress_cart.setVisibility(View.GONE);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("userid", SharedPref.getUserId(CartActivity.this));
                params.put("coupon_code", coupon_code);
                params.put("min_value", amount);
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CHOOSE_ADDRESS_CODE) {

            if(data!=null && data.getStringExtra(Constants.ADDRESS)!=null && !data.getStringExtra(Constants.ADDRESS).equals("")) {
                address = data.getStringExtra(Constants.ADDRESS);
                address_id = data.getStringExtra("address_id");
                area_delivery = data.getStringExtra("delivery_amount");
                addressModel = new Address();
                addressModel.setFullAddress(address);
                deliveraddress.setText(address);
                del_label.setText(getResources().getString(R.string.deliver_to));

                tvDelivery.setText(SharedPref.getCurrency(CartActivity.this)+area_delivery);

                if (checkWallet.isChecked()) {
                    if (Constants.WALLET_FLAG) {
                        cwall = wallet;
                        String payamt = null;

                        String[] splited = tvTotal.getText().toString().split(Method.escapeSpecialRegexChars(SharedPref.getCurrency(CartActivity.this)));
                        try {
                            payamt = splited[1];
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (Double.valueOf(payamt) >= cwall && cwall != 0)
                        {
                            totalamount = Double.valueOf(payamt) - cwall;
                            totalamount = Double.valueOf(payamt) - cwall;
                            DecimalFormat df = new DecimalFormat("#.##");
                            totalamount= Double.parseDouble(df.format(Double.parseDouble(String.valueOf(totalamount))));
                          tvTotal.setText(SharedPref.getCurrency(CartActivity.this) + totalamount);
                            linearWallet.setVisibility(View.VISIBLE);
                            txtWallet.setText("-" + SharedPref.getCurrency(CartActivity.this) + String.valueOf(cwall));
                            txtWallet.setTextColor(getResources().getColor(R.color.colorRed));
                        }
                        else if (Double.valueOf(payamt) <= cwall && cwall != 0) {
                            tvTotal.setText(SharedPref.getCurrency(CartActivity.this) + " 0");
                            cwall = Double.parseDouble(payamt);
                            linearWallet.setVisibility(View.VISIBLE);
                            txtWallet.setText("-" + SharedPref.getCurrency(CartActivity.this) + String.valueOf(cwall));
                            txtWallet.setTextColor(getResources().getColor(R.color.colorRed));
                        }
                    }
                }

            }

        }
    }

    public void getCartList() {
        progress_cart.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        tvNodata.setVisibility(View.GONE);
        main_rel.setVisibility(View.GONE);
        final RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.cart_list+"&user_id="+ SharedPref.getUserId(CartActivity.this), new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {

                carts=new ArrayList<>();
                try {
                    DecimalFormat df = new DecimalFormat("#.##");
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArrayTotal= jsonObject.getJSONArray("TOTAL_PRICE");
                    for (int i=0;i<jsonArrayTotal.length();i++)
                    {
                        JSONObject jsonObject1 = jsonArrayTotal.getJSONObject(i);
                        gst=jsonObject1.getString("gst");
                        total_price = jsonObject1.getString("total_price");
                        txtGst.setText(SharedPref.getCurrency(CartActivity.this)+gst);
                    }

                    JSONObject jsonArrAddress= jsonObject.getJSONObject("ADDRESS");
                    delivery_charges = jsonArrAddress.getString("delivery_amount");

               tvTotal.setText(SharedPref.getCurrency(CartActivity.this) + df.format(Double.parseDouble(total_price) + Double.parseDouble(gst)));
                        //+Double.parseDouble(delivery_charges)
                        if (checkWallet.isChecked()) {
                            if (Constants.WALLET_FLAG) {
                                cwall = wallet;
                                String payamt = null;

                                String[] splited = tvTotal.getText().toString().split(Method.escapeSpecialRegexChars(SharedPref.getCurrency(CartActivity.this)));
                                try {
                                    payamt = splited[1];
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (Double.valueOf(payamt) >= cwall && cwall != 0)
                                {
                                    totalamount = Double.valueOf(payamt) - cwall;
                                    totalamount= Double.parseDouble(df.format(Double.parseDouble(String.valueOf(totalamount))));
                                    tvTotal.setText(SharedPref.getCurrency(CartActivity.this) + totalamount);
                                    linearWallet.setVisibility(View.VISIBLE);
                                    txtWallet.setText("-" + SharedPref.getCurrency(CartActivity.this) + String.valueOf(cwall));
                                    txtWallet.setTextColor(getResources().getColor(R.color.colorRed));
                                }
                                else if (Double.valueOf(payamt) <= cwall && cwall != 0)
                                {
                                    tvTotal.setText(SharedPref.getCurrency(CartActivity.this) + " 0");
                                    cwall = Double.parseDouble(payamt);
                                    linearWallet.setVisibility(View.VISIBLE);
                                    txtWallet.setText("-" + SharedPref.getCurrency(CartActivity.this) + String.valueOf(cwall));
                                    txtWallet.setTextColor(getResources().getColor(R.color.colorRed));
                                }
                            }
                        }
                        btnCheckout.setEnabled(true);
                       tvDelivery.setText(SharedPref.getCurrency(CartActivity.this)+delivery_charges);
                        tvSubPrice.setText(SharedPref.getCurrency(CartActivity.this)+total_price);

                    JSONArray jsonArray = jsonObject.getJSONArray("VIEW_CART");
                    for (int j=0;j<jsonArray.length();j++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                        String id = jsonObject1.getString("id");
                        String user_id = jsonObject1.getString("user_id");
                        String category_id = jsonObject1.getString("category_id");
                        String menu_id = jsonObject1.getString("menu_id");
                        String menu_name = jsonObject1.getString("menu_name");
                        String menu_image = jsonObject1.getString("menu_image");
                        String variant_id = jsonObject1.getString("variant_id");
                        String variant_volume_type = jsonObject1.getString("variant_volume_type");
                        String variant_price = jsonObject1.getString("variant_price");
                        String variant_qty = jsonObject1.getString("variant_qty");
                        String volume_qty = jsonObject1.getString("volume_qty");
                        ArrayList<Toppings> toppings = new ArrayList<>();
                        if (jsonObject1.has("topping_list")) {
                            JSONArray jsonArray1 = jsonObject1.getJSONArray("topping_list");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                                if (!jsonObject2.has("message")){
                                    String topping_id = jsonObject2.getString("topping_id");
                                    String topping_name = jsonObject2.getString("topping_name");
                                    String price = jsonObject2.getString("price");
                                    toppings.add(new Toppings(topping_id, topping_name, price));
                                }
                            }
                        }
                        carts.add(new Cart(id,user_id,category_id,menu_id,menu_name,menu_image,variant_id,variant_volume_type,variant_price,variant_qty,volume_qty,toppings));
                    }

                    if (coupon_code!=null && !coupon_code.equals("") && !discount.equalsIgnoreCase("")){
                        getDiscount(coupon_code, String.valueOf(Double.parseDouble(total_price)));
                        //+ Double.parseDouble(delivery_charges)
                    }

                    if (carts.size()>0){
                        flag = true;
                        invalidateOptionsMenu();
                    }else{
                        flag = false;
                        invalidateOptionsMenu();
                    }

                    adapter=new CartAdapter(CartActivity.this,carts);
                    recycler_cart.setAdapter(adapter);

                    progress_cart.setVisibility(View.GONE);
                    tvNodata.setVisibility(View.GONE);
                    loader.setVisibility(View.GONE);
                    main_rel.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    tvNodata.setVisibility(View.VISIBLE);
                    main_rel.setVisibility(View.GONE);
                    loader.setVisibility(View.GONE);
                    progress_cart.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestQueue.stop();
                progress_cart.setVisibility(View.GONE);
                loader.setVisibility(View.GONE);
                main_rel.setVisibility(View.GONE);
                tvNodata.setVisibility(View.VISIBLE);
            }
        });
        requestQueue.add(stringRequest);
    }

    @Override
    public void onRefresh()
    {
        getCartList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
