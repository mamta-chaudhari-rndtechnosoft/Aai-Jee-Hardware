package com.aaijee.app.Activity;

import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaijee.app.Adapter.SearchMenuAdapter;
import com.aaijee.app.Model.FlavourList;
import com.aaijee.app.Model.MenuList;
import com.aaijee.app.Model.SubMenuList;
import com.aaijee.app.Model.VariantList;
import com.aaijee.app.R;
import com.aaijee.app.Util.Constants;
import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView textView_empty;
    String search;
    ArrayList<MenuList> menuLists;
    private MenuItem item;
    private SearchView searchView;
    LottieAnimationView loader,empty_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_search);

        toolbar = findViewById(R.id.toolbar_search);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setTitle(Html.fromHtml("<b>"+getResources().getString(R.string.search)+"</b>"));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textView_empty = findViewById(R.id.tv_empty_search);
        textView_empty.setVisibility(View.VISIBLE);
        textView_empty.setText(R.string.type_search);
        loader = findViewById(R.id.loader);
        empty_icon = findViewById(R.id.empty_icon);
        loader.setVisibility(View.GONE);
        empty_icon.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.rv_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.search, menu);
        menu.findItem(R.id.search).setVisible(true);
        item = menu.findItem(R.id.search);
        item.expandActionView();
         searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        //searchView.setOnQueryTextListener(queryTextListener);
        menuLists = new ArrayList<>();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed()
    {
        searchView.onActionViewCollapsed();
        searchView.setIconified(false);
        searchView.clearFocus();
        invalidateOptionsMenu();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case android.R.id.home:
                searchView.onActionViewCollapsed();
                searchView.setIconified(false);
                searchView.clearFocus();
                invalidateOptionsMenu();
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    protected void onStop() {
       // Tovuti.from(this).stop();
        super.onStop();
    }

    /*SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            search=s.trim();
            if (!search.equals("")) {
                loader.setVisibility(View.VISIBLE);
                Tovuti.from(SearchActivity.this).monitor(new Monitor.ConnectivityListener(){
                    @Override
                    public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast){
                        // TODO: Handle the connection...
                        if (isConnected) {
                            getSearchList(search);
                        }
                        else {
                            Toast.makeText(SearchActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            try {
                InputMethodManager imm = (InputMethodManager)SearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                // TODO: handle exception
            }
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            search=s.trim();
            if (!search.equals("")) {
                loader.setVisibility(View.VISIBLE);
                Tovuti.from(SearchActivity.this).monitor(new Monitor.ConnectivityListener(){
                    @Override
                    public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast){
                        // TODO: Handle the connection...
                        if (isConnected) {
                            getSearchList(search);
                        }
                        else {
                            Toast.makeText(SearchActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            return false;
        }
    };*/

    private void getSearchList(String search) {
        loader.setVisibility(View.VISIBLE);
        empty_icon.setVisibility(View.GONE);
        final RequestQueue requestQueue = Volley.newRequestQueue(SearchActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.search_list+"="+search+"&page=1", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

               menuLists.clear();
                try {
                    JSONObject json = new JSONObject(response);


                    JSONArray jsonArray = json.getJSONArray("SEARCH_LIST");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        String id = object.getString("id");
                        String name = object.getString("name");
                        String image = object.getString("image");
                        String discount = object.getString("discount");
                        String food_type_icon = object.getString("food_type_icon");
                        String des = object.getString("des");
                        String food_opening_time = object.getString("food_opening_time");
                        String food_closing_time = object.getString("food_closing_time");
                        String food_time_msg  = object.getString("food_time_msg");

                            JSONArray arrayVariant = object.getJSONArray("variant_list");
                            ArrayList<VariantList> variantLists = new ArrayList<>();

                            for (int j = 0; j < arrayVariant.length(); j++) {
                                JSONObject jsonObject = arrayVariant.getJSONObject(j);
                                String id_variant = jsonObject.getString("id");
                                String volume = jsonObject.getString("volume");
                                String price = jsonObject.getString("price");
                                String cart_status = jsonObject.getString("cart_status");
                                String variant_qty = jsonObject.getString("variant_qty");
                                String volume_qty = jsonObject.getString("volume_qty");

                                JSONArray arraySubMenu = jsonObject.getJSONArray("sub_menu_list");
                                ArrayList<SubMenuList> subMenuLists = new ArrayList<>();
                                for (int l = 0; l < arraySubMenu.length(); l++) {
                                    JSONObject jsonObject1 = arraySubMenu.getJSONObject(l);
                                    if (jsonObject1.getString("error").equals("False")) {
                                        String id_submenu = jsonObject1.getString("id");
                                        String sub_menu = jsonObject1.getString("sub_menu");
                                        String pricesub = jsonObject1.getString("price");
                                        String status = jsonObject1.getString("topping_status");
                                        subMenuLists.add(new SubMenuList(id_submenu, sub_menu, pricesub, status));
                                    }
                                }


                                variantLists.add(new VariantList(id_variant, volume, price, cart_status, variant_qty, volume_qty, subMenuLists));
                            }

                            menuLists.add(new MenuList(id, "", name, image, food_type_icon, des, food_opening_time, food_closing_time, food_time_msg, "",discount, variantLists, new ArrayList<FlavourList>()));
                    }
                    if (menuLists.size()>0) {
                        loader.setVisibility(View.GONE);
                        textView_empty.setVisibility(View.GONE);
                        empty_icon.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView.setAdapter(new SearchMenuAdapter(SearchActivity.this, menuLists));
                    }else{
                        loader.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        textView_empty.setText("No items found");
                        textView_empty.setVisibility(View.VISIBLE);
                        empty_icon.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loader.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    textView_empty.setVisibility(View.VISIBLE);
                    textView_empty.setText("No items found");
                    empty_icon.setVisibility(View.VISIBLE);
                   //Toast.makeText(SearchActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                requestQueue.stop();
                loader.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                textView_empty.setVisibility(View.VISIBLE);
            }
        });
        requestQueue.add(stringRequest);
    }

}
