package com.aaijee.app.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aaijee.app.Activity.MenuDetailActivity;
import com.aaijee.app.Model.MenuList;
import com.aaijee.app.Model.SubMenuList;
import com.aaijee.app.Model.VariantList;
import com.aaijee.app.R;
import com.aaijee.app.Util.Constants;
import com.aaijee.app.Util.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubMenuDetailAdapter extends RecyclerView.Adapter<SubMenuDetailAdapter.ViewHolder> {

    private Activity activity;
    ArrayList<SubMenuList> subMenuLists;
    MenuList menuList;
    VariantList variantList;

    public SubMenuDetailAdapter(Activity activity, MenuList menuList, VariantList variantList, ArrayList<SubMenuList> subMenuLists) {
        this.activity = activity;
        this.menuList = menuList;
        this.variantList = variantList;
        this.subMenuLists = subMenuLists;
    }

    @Override
    public SubMenuDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.submenu_item, parent, false);

        return new SubMenuDetailAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final SubMenuDetailAdapter.ViewHolder holder, final int position) {
        holder.tvsubmenu_name.setText(subMenuLists.get(position).getSub_menu());
        holder.tvPrice.setText(SharedPref.getCurrency(activity)+" "+ subMenuLists.get(position).getPrice());
        if (subMenuLists.get(position).getStatus().equals("True")){
            holder.checkbox.setChecked(true);
        }else{
            holder.checkbox.setChecked(false);
        }

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = holder.checkbox.isChecked();
                if (checked){
                    //remove topping
                    addcart(Constants.CHECKED,menuList.getCat_id(),menuList.getId(),menuList.getName(),menuList.getImage(),variantList.getId(),variantList.getVolume(),variantList.getPrice(),subMenuLists.get(position).getId(),subMenuLists.get(position).getSub_menu(),subMenuLists.get(position).getPrice());
                }else{
                    //add topping
                    addcart(Constants.UNCHECKED,menuList.getCat_id(),menuList.getId(),menuList.getName(),menuList.getImage(),variantList.getId(),variantList.getVolume(),variantList.getPrice(),subMenuLists.get(position).getId(),subMenuLists.get(position).getSub_menu(),subMenuLists.get(position).getPrice());
                }
            }
        });
    }

    private void addcart(final String status, final String cat_id, final String id, final String name, final String image, final String variant_id, final String volume, final String price, final String submenu_id, final String topping_name, final String topping_price) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.add_cart,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Response
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("MANAGE_CART");
                            String message = jsonObject1.getString("message");
                            MenuDetailActivity detailActivity = (MenuDetailActivity) activity;
                            detailActivity.cartcount();
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
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
                params.put("user_id", SharedPref.getUserId(activity));
                params.put("category_id", cat_id);
                params.put("menu_id", id);
                params.put("menu_name", name);
                params.put("menu_image", image);
                params.put("variant_id", variant_id);
                params.put("variant_type", volume);
                params.put("variant_price", price);
                params.put("variant_qty", Constants.QUANTITY);
                params.put("operation", status);
                params.put("sub_menu_id", submenu_id);
                params.put("topping_name", topping_name);
                params.put("topping_price", topping_price);
                return params;
            }
        };
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return subMenuLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvsubmenu_name,tvPrice;
        CheckBox checkbox;

        public ViewHolder(View itemView) {
            super(itemView);

            tvsubmenu_name = (TextView) itemView.findViewById(R.id.tvsubmenu_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tvprice);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);

        }
    }
}