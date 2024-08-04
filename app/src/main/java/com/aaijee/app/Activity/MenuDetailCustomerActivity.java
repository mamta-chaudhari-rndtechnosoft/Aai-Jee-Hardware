package com.aaijee.app.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.aaijee.app.Model.FlavourList;
import com.aaijee.app.Model.MenuList;
import com.aaijee.app.Model.SubMenuList;
import com.aaijee.app.Model.VariantList;
import com.aaijee.app.R;
import com.aaijee.app.Util.Constants;
import com.aaijee.app.Util.Method;
import com.aaijee.app.Util.SharedPref;
import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuDetailCustomerActivity extends AppCompatActivity {

    String menu_id, menu_name, menu_image;
    ImageView itemImage, foodType, decrease, increase;
    TextView tvToolbarTitle, tvCartCount, tvMenu, tvPrice, tvTotalPrice, tvAddtoCart, count, tvDiscountPer, tvDiscount;
    TextView button_toggle, tvDescr;
    TextView tvDesc;
    Toolbar toolbar;
    ProgressBar progress_menu;
    RelativeLayout btnGotoCart;
    LinearLayout r_addcart;
    String id, name, image, food_type_icon, des, discount, cat_id = "";
    ArrayList<VariantList> variantLists;
    private MenuList menuList;
    private ImageView imgNotification, imgCart;
    private String cart_status, variant_qty, variant_id, volume, price;
    CardView btnContinue;
    LottieAnimationView loader;
    // ScrollView scroll_lay;
    private String food_opening_time, food_closing_time, food_time_msg, get_qty;
    String endtime = "";
    String starttime = "";
    Spinner spinner;
    int var_pos = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_menu_detail_customer);

        Intent in = getIntent();

        //get_qty=in.getStringExtra("qty");
        menu_id = in.getStringExtra("menu_id");
        menu_name = in.getStringExtra("menu_name");
        menu_image = in.getStringExtra("menu_image");
        cat_id = in.getStringExtra("cat_id");

        Log.d("TAG", "menu" + menu_id);
        Log.d("TAG", "menu_name" + menu_name);
        Log.d("TAG", "menu_image" + menu_image);
        Log.d("TAG", "cat_id" + cat_id);

        if (cat_id == null) {
            cat_id = "";
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_main_customer);
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitleCustomer);
        tvAddtoCart = (TextView) findViewById(R.id.tvAddtoCart);
        count = (TextView) findViewById(R.id.count);
        tvDiscountPer = (TextView) findViewById(R.id.tvDiscountPer);
        tvDiscount = (TextView) findViewById(R.id.tvDiscount);
        tvCartCount = (TextView) findViewById(R.id.tvCartCount);
        btnContinue = findViewById(R.id.btnContinue);
        loader = findViewById(R.id.loader);
        spinner = findViewById(R.id.spinner_variant);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        tvToolbarTitle.setText(Html.fromHtml(" "));

        //   progress_menu = findViewById(R.id.progress_menu);
        setSupportActionBar(toolbar);

        // scroll_lay = findViewById(R.id.scroll_lay);
        itemImage = findViewById(R.id.itemImage);
        foodType = findViewById(R.id.foodType);
        tvMenu = findViewById(R.id.tvMenu);
        tvPrice = findViewById(R.id.tvPrice);
        tvDesc = findViewById(R.id.tvDesc);

        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnGotoCart = findViewById(R.id.btnGotoCart);
        r_addcart = findViewById(R.id.r_addcart);
        btnGotoCart.setVisibility(View.GONE);
        imgCart = (ImageView) findViewById(R.id.imgCart);
        imgNotification = (ImageView) findViewById(R.id.imgNotification);
        //decrease = (ImageView) findViewById(R.id.decrease);
        // increase = (ImageView) findViewById(R.id.increase);
        button_toggle = (TextView) findViewById(R.id.button_toggle);
        tvDescr = (TextView) findViewById(R.id.tvDescr);
        button_toggle.setVisibility(View.VISIBLE);
//        scroll_lay.setVisibility(View.GONE);

        variantLists = new ArrayList<>();

       /* btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuDetailActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            }
        });*/


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {

                price = variantLists.get(i).getPrice();
                var_pos = i;
                tvPrice.setText(SharedPref.getCurrency(MenuDetailCustomerActivity.this) + price);

//                String qty=variantLists.get(i).getQty();
//                count.setText(qty);

                if (discount.equalsIgnoreCase("0")) {
                    tvDiscount.setVisibility(View.GONE);
                    tvDiscountPer.setVisibility(View.GONE);
                    tvPrice.setText(SharedPref.getCurrency(MenuDetailCustomerActivity.this) + price);
                }
                else
                {
                    tvDiscount.setVisibility(View.VISIBLE);
                    tvDiscountPer.setVisibility(View.VISIBLE);
                    double actual_price = Double.parseDouble(price);
                    double disc_per = Double.parseDouble(discount);
                    double disc_amt = (actual_price * disc_per) / 100;
                    double disc_price = actual_price - disc_amt;
                    tvDiscount.setText(SharedPref.getCurrency(MenuDetailCustomerActivity.this) + price);
                    tvDiscount.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvPrice.setText(SharedPref.getCurrency(MenuDetailCustomerActivity.this) + disc_price);
                    tvDiscountPer.setText(discount + "%");
                }
                // onRefresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        tvMenu.setText(menu_name);
        Glide.with(MenuDetailCustomerActivity.this).load(menu_image).thumbnail(Glide.with(this).load(R.drawable.loading)).into(itemImage);

        btnGotoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuDetailCustomerActivity.this, CartActivity.class));
            }
        });

        count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method();
            }
        });

        tvAddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method();
            }
        });

       /* button_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (tvDesc.isExpanded()) {
                    tvDesc.collapse();
                    button_toggle.setText("more..");
                } else {
                    tvDesc.expand();
                    button_toggle.setText("less..");
                }
            }
        });*/

        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuDetailCustomerActivity.this, NotificationActivity.class));
            }
        });

        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuDetailCustomerActivity.this, CartActivity.class));
            }
        });

    }

    private void method() {

        final Dialog dialog = new Dialog(MenuDetailCustomerActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.et_quantity);

        EditText mInput;
        TextView mActionOk;
        CircleImageView mActionCancel;
        TextView tvname;

        mActionCancel = dialog.findViewById(R.id.action_cancel);
        mActionOk = dialog.findViewById(R.id.action_ok);
        tvname = dialog.findViewById(R.id.product_name);
        // image=dialog.findViewById(R.id.image);
        mInput = dialog.findViewById(R.id.input);


        tvname.setText(name);

        if (count.getText().toString().equals("")) {

        } else {
            mInput.setText(count.getText().toString());
        }
        //String qty=mInput.getText().toString();

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String qty = mInput.getText().toString();

                Log.d("hirnikiqty", "onClick: " + qty);

                r_addcart.setVisibility(View.VISIBLE);

                if (qty.equals("") || qty.equals("0")) {
                    //tvAddtoCart.setVisibility(View.VISIBLE);
                    Log.d("if", "if k andar");
                } else {
                    Log.d("else", "else k andar");
                    tvAddtoCart.setVisibility(View.GONE);
                    count.setText(qty);
                }

                String final_price = null;
                String[] splited = tvPrice.getText().toString().split(Method.escapeSpecialRegexChars(SharedPref.getCurrency(MenuDetailCustomerActivity.this)));
                try {
                    final_price = splited[1];
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addtocart(cat_id, id, name, image, variantLists.get(var_pos).getId(), variantLists.get(var_pos).getVolume(), final_price, variantLists.get(var_pos).getVolume_qty(), Constants.INCREMENT);
                //}
                dialog.dismiss();

            }
        });

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qty = mInput.getText().toString();

                dialog.dismiss();

            }
        });


        dialog.show();


    }

    private void addtocart(final String cat_id, final String id, final String name, final String image, final String variant_id, final String volume, final String price, final String volume_qty, final String operation) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.add_cart,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Response

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONObject jsonObject1 = jsonObject.getJSONObject("MANAGE_CART");
                            String message = jsonObject1.getString("message");
                            //cartcount();
                            Toast.makeText(MenuDetailCustomerActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Error

                    }
                }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", SharedPref.getUserId(MenuDetailCustomerActivity.this));
                params.put("category_id", cat_id);
                params.put("menu_id", id);
                params.put("menu_name", name);
                params.put("menu_image", image);
                params.put("variant_id", variant_id);
                params.put("variant_type", volume);
                params.put("variant_price", price);
                params.put("variant_qty", count.getText().toString());
                params.put("volume_qty", volume_qty);
                params.put("operation", operation);
                return params;
            }
        };
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(MenuDetailCustomerActivity.this);
        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //cartcount();
        menuList = null;
        variantLists = new ArrayList<>();
        getMenuDetail();

    }

    private void getMenuDetail() {

        loader.setVisibility(View.VISIBLE);
        //  progress_menu.setVisibility(View.GONE);

        final RequestQueue requestQueue = Volley.newRequestQueue(MenuDetailCustomerActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.menu_detail + "&menu_id=" + menu_id + "&user_id=" + SharedPref.getUserId(MenuDetailCustomerActivity.this), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Log.d("menu", "onResponse: " + response);

                    JSONArray jsonArray = jsonObject.getJSONArray("MAIN_MENU_LIST");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        id = object.getString("id");
                        name = object.getString("name");
                        image = object.getString("image");
                        food_type_icon = object.getString("food_type_icon");
                        des = object.getString("des");
                        discount = object.getString("discount");
                        food_opening_time = object.getString("food_opening_time");
                        food_closing_time = object.getString("food_closing_time");
                        food_time_msg = object.getString("food_time_msg");

                        JSONArray jsonVariant = object.getJSONArray("variant_list");
                        for (int j = 0; j < jsonVariant.length(); j++) {
                            JSONObject object1 = jsonVariant.getJSONObject(j);
                            variant_id = object1.getString("id");
                            volume = object1.getString("volume");
                            //mrp here
                           // price = object1.getString("price");
                            price = object1.getString("mrp");
                            cart_status = object1.getString("cart_status");
                            variant_qty = object1.getString("variant_qty");
                            String volume_qty = object1.getString("volume_qty");


                            if (j == 0 && (!variant_qty.equals("0") || !variant_qty.equals(" ") || !variant_qty.equals("null"))) {
                                //tvPrice.setText(variant_qty);
                                Log.d("condition", "onResponse: " + variant_qty);
                                Log.d("condition", "if k andar");
                                count.setText(variant_qty);
                                tvAddtoCart.setVisibility(View.GONE);

                                //Toast.makeText(MenuDetailActivity.this, "variant_qty"+variant_qty, Toast.LENGTH_SHORT).show();
                            }
//                         else{
//                             Toast.makeText(MenuDetailActivity.this, "variant_qty"+variant_qty, Toast.LENGTH_SHORT).show();
//
//                             tvAddtoCart.setVisibility(View.VISIBLE);
//                             r_addcart.setVisibility(View.GONE);
//                         }

                            Log.d("condition", "variant_qty" + variant_qty);


                            ArrayList<SubMenuList> subMenuLists = new ArrayList<>();
                            JSONArray jsonSubmenu = object1.getJSONArray("sub_menu_list");
                            for (int k = 0; k < jsonSubmenu.length(); k++) {
                                JSONObject object12 = jsonSubmenu.getJSONObject(k);
                                if (object12.getString("error").equals("False")) {
                                    String sub_id = object12.getString("id");
                                    String sub_menu = object12.getString("sub_menu");
                                    //mrp here
                                    //String pricesub = object12.getString("price");
                                    String pricesub = object12.getString("mrp");
                                    String status = object12.getString("topping_status");
                                    subMenuLists.add(new SubMenuList(sub_id, sub_menu, pricesub, status));
                                }
                            }
                            variantLists.add(new VariantList(variant_id, volume, price, cart_status, variant_qty, volume_qty, subMenuLists));
                        }

                        menuList = new MenuList(id, cat_id, name, image, food_type_icon, des, food_opening_time, food_closing_time, food_time_msg, "", discount, variantLists, new ArrayList<FlavourList>());

                        final ArrayList<String> variant_list = new ArrayList<>();
                        for (int k = 0; k < variantLists.size(); k++) {
                            String variant = variantLists.get(k).getVolume_qty() + " " + variantLists.get(k).getVolume();
                            variant_list.add(variant);
                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MenuDetailCustomerActivity.this, android.R.layout.simple_spinner_item, variant_list);

                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(dataAdapter);

                    }

                    tvMenu.setText(name);
                    if (des != null && !des.equals("")) {
                        tvDesc.setText(Html.fromHtml(des));
                    } else {
                        tvDescr.setVisibility(View.GONE);
                        tvDesc.setVisibility(View.GONE);
                        button_toggle.setVisibility(View.GONE);
                    }
                    Glide.with(MenuDetailCustomerActivity.this).load(image).thumbnail(Glide.with(MenuDetailCustomerActivity.this).load(R.drawable.loading)).into(itemImage);
                    Glide.with(MenuDetailCustomerActivity.this).load(food_type_icon).thumbnail(Glide.with(MenuDetailCustomerActivity.this).load(R.drawable.loading)).into(foodType);

                    if (des.length() < 250) {
                        button_toggle.setVisibility(View.GONE);
                    }

//                    if (cart_status.equalsIgnoreCase("false")){
//                        count.setText("0");
//                       // r_addcart.setVisibility(View.GONE);
//                       // tvAddtoCart.setVisibility(View.VISIBLE);
//                    }else{
//                        r_addcart.setVisibility(View.VISIBLE);
//                      //  tvAddtoCart.setVisibility(View.GONE);
//                        count.setText(variant_qty);
//                    }

                    loader.setVisibility(View.GONE);
                    //  scroll_lay.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    loader.setVisibility(View.GONE);
                    // scroll_lay.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestQueue.stop();
                // scroll_lay.setVisibility(View.GONE);
                loader.setVisibility(View.GONE);
            }
        });
        requestQueue.add(stringRequest);
    }

    public void cartcount() {
        Method.getCartCount(MenuDetailCustomerActivity.this, tvCartCount);
        if (Integer.parseInt(Constants.CART_COUNT) > 0) {
            tvCartCount.setVisibility(View.VISIBLE);
            tvCartCount.setText(Constants.CART_COUNT);
        } else {
            tvCartCount.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}