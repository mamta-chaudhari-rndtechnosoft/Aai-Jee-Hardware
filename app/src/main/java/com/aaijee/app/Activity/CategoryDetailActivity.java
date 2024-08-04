package com.aaijee.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.aaijee.app.Adapter.CategoryPagerAdapter;
import com.aaijee.app.Fragment.CategoryFragment;
import com.aaijee.app.Model.CategoryList;
import com.aaijee.app.Model.CategoryMenuList;
import com.aaijee.app.R;
import com.aaijee.app.Util.Constants;
import com.aaijee.app.Util.Method;
import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryDetailActivity extends AppCompatActivity {

    private TabLayout tab;
    public ViewPager viewPager;
    Toolbar toolbar;
    ProgressBar progress;
    TextView txtNoData;
    public ArrayList<CategoryMenuList> menuLists;
    public ArrayList<CategoryList> categoryLists;
    TextView tvToolbarTitle,tvCartCount,tvTotalPrice;
    private ImageView imgNotification,imgCart;
    private int selectPosition;
    private String cat_id;
    RelativeLayout cart_lay;
    LottieAnimationView loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_category_detail);

        toolbar =(Toolbar) findViewById(R.id.toolbar_main);
        tvToolbarTitle =(TextView) findViewById(R.id.tvToolbarTitle);
        tvCartCount =(TextView) findViewById(R.id.tvCartCount);
        tvTotalPrice =(TextView) findViewById(R.id.tvTotalPrice);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        tvToolbarTitle.setText("All Categories");
        setSupportActionBar(toolbar);


        Intent in = getIntent();
        selectPosition = in.getIntExtra("position", 0);
        cat_id = in.getStringExtra("cat_id");

        //Toast.makeText(this, "potion: " + selectPosition + " catId: " + cat_id, Toast.LENGTH_SHORT).show();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        imgCart = (ImageView) findViewById(R.id.imgCart);
        imgNotification = (ImageView) findViewById(R.id.imgNotification);
        tab = (TabLayout) findViewById(R.id.tabs);
        progress=findViewById(R.id.progresbar_rest);
        txtNoData=findViewById(R.id.txtNoData);
        cart_lay=findViewById(R.id.cart_lay);
        loader=findViewById(R.id.loader);
        cart_lay.setVisibility(View.GONE);
        tab.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(
                tab));
        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryDetailActivity.this,NotificationActivity.class));
            }
        });

        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryDetailActivity.this,CartActivity.class));
            }
        });

        cart_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryDetailActivity.this,CartActivity.class));
            }
        });

        getCategory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartcount();
    }

    public void cartcount() {
        Method.getCartCount(CategoryDetailActivity.this,tvCartCount);
       Method.getTotalPrice(CategoryDetailActivity.this,tvTotalPrice,cart_lay);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void getCategory()
    {
        menuLists=new ArrayList<>();
        categoryLists=new ArrayList<>();
        progress.setVisibility(View.GONE);
        txtNoData.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        final RequestQueue requestQueue = Volley.newRequestQueue(CategoryDetailActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.category, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("CATEGORY_LIST");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        String id = object.getString("id");
                        String category_name = object.getString("category_name");
                        String category_image = object.getString("image");
                        String count = object.getString("count");
                        categoryLists.add(new CategoryList(id,category_name,category_image,count));
                    }
                    setTab();
                    progress.setVisibility(View.GONE);
                    loader.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.setVisibility(View.GONE);
                    loader.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.GONE);
                    tab.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestQueue.stop();
                loader.setVisibility(View.GONE);
            }
        });
        requestQueue.add(stringRequest);
    }

    private void setTab() {

        if (categoryLists!=null && categoryLists.size()>0) {
            setupViewPager(viewPager);

            tab.setupWithViewPager(viewPager);
        }else {
            txtNoData.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
            tab.setVisibility(View.GONE);
        }

        for (int i = 0; i < tab.getTabCount(); i++) {
            tab.getTabAt(i).setCustomView(R.layout.tab_custom_view);
            TextView tab_name = (TextView) tab.getTabAt(i).getCustomView().findViewById(R.id.tab_text);
          //  ImageView tab_img = (ImageView) tab.getTabAt(i).getCustomView().findViewById(R.id.tab_img);
            tab_name.setText(categoryLists.get(i).getCategory_name());
            tab_name.setLines(5);

           // Glide.with(CategoryDetailActivity.this).load(categoryLists.get(i).getCategory_image()).thumbnail(Glide.with(this).load(R.drawable.loading)).into(tab_img);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        CategoryPagerAdapter adapter = new CategoryPagerAdapter(getSupportFragmentManager());

        for(int i=0;i<categoryLists.size();i++) {
            CategoryFragment categoryFragment = new CategoryFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id",categoryLists.get(i).getId());
            bundle.putString("category_name",categoryLists.get(i).getCategory_name());
            categoryFragment.setArguments(bundle);
            adapter.addFragment(categoryFragment, categoryLists.get(i).getCategory_name());
            if (cat_id!=null && cat_id.equals(categoryLists.get(i).getId()))
                selectPosition = i;
        }
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(selectPosition);
    }
}
