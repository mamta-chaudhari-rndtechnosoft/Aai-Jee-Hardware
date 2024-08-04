package com.aaijee.app.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaijee.app.Activity.CategoryDetailActivity;
import com.aaijee.app.Activity.MenuDetailCustomerActivity;
import com.aaijee.app.Model.MenuList;
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
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryMenuCustomerAdapter extends RecyclerView.Adapter<CategoryMenuCustomerAdapter.ViewHolder> {

    private Activity activity;
    private List<MenuList> menuLists;
    String endtime = "";
    String starttime = "";

    public CategoryMenuCustomerAdapter(Activity activity, List<MenuList> menuLists) {
        this.activity = activity;
        this.menuLists = menuLists;
    }

    @NonNull
    @Override
    public CategoryMenuCustomerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.new_menu_item, parent, false);
        return new CategoryMenuCustomerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryMenuCustomerAdapter.ViewHolder holder, int position) {

        final String[] price = {""};
        final int[] var_pos = {0};
        Glide.with(activity).load(menuLists.get(position).getImage()).thumbnail(Glide.with(activity).load(R.drawable.loading).centerCrop()).centerCrop().into(holder.imgMenu);
        holder.tvName.setText(menuLists.get(position).getName());

        if (menuLists.get(position).getDes().equalsIgnoreCase("") || menuLists.get(position).getDes() == null)
            holder.tvDesc.setVisibility(View.GONE);

        holder.tvDesc.setText(Html.fromHtml(menuLists.get(position).getDes()));

        holder.tvPrice.setText(SharedPref.getCurrency(activity) + menuLists.get(position).getVariantLists().get(0).getPrice());

        GradientDrawable gd = new GradientDrawable();
        if (SharedPref.getDASHED(activity).equalsIgnoreCase("1")) {
            gd.setColor(activity.getResources().getColor(R.color.colorWhite));
            gd.setCornerRadius(25);
            gd.setStroke(4, activity.getResources().getColor(R.color.colorGrey1), 12, 16);
        } else {
            gd.setColor(activity.getResources().getColor(R.color.colorWhite));
            gd.setCornerRadius(25);
            gd.setStroke(4, activity.getResources().getColor(R.color.colorGrey1));
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MenuDetailCustomerActivity.class);
                intent.putExtra("menu_image", menuLists.get(position).getImage());
                intent.putExtra("menu_name", menuLists.get(position).getName());
                intent.putExtra("menu_id", menuLists.get(position).getId());
                intent.putExtra("cat_id", menuLists.get(position).getCat_id());
                activity.startActivity(intent);
            }
        });

        holder.tvQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method(holder, position, holder.tvQty.getText().toString(), var_pos);
            }
        });

        holder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MenuDetailCustomerActivity.class);
                intent.putExtra("menu_image", menuLists.get(position).getImage());
                intent.putExtra("menu_name", menuLists.get(position).getName());
                intent.putExtra("menu_id", menuLists.get(position).getId());
                intent.putExtra("cat_id", menuLists.get(position).getCat_id());
                activity.startActivity(intent);
            }
        });

        final ArrayList<String> variant_list = new ArrayList<>();
        for (int i = 0; i < menuLists.get(position).getVariantLists().size(); i++) {
            String variant = menuLists.get(position).getVariantLists().get(i).getVolume_qty() + " " + menuLists.get(position).getVariantLists().get(i).getVolume();
            variant_list.add(variant);
        }
        //-------------------

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, variant_list);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner_variant.setAdapter(dataAdapter);

        holder.spinner_variant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                var_pos[0] = i;
                price[0] = menuLists.get(position).getVariantLists().get(i).getPrice();

                holder.tvPrice.setText(SharedPref.getCurrency(activity) + price[0]);

                holder.tvQty.setText(menuLists.get(position).getVariantLists().get(i).getQty());
                if (menuLists.get(position).getDiscount().equalsIgnoreCase("0")) {
                    holder.tvDiscountPer.setVisibility(View.GONE);
                    holder.tvDiscount.setVisibility(View.GONE);
                    holder.tvPrice.setText(SharedPref.getCurrency(activity) + price[0]);
                } else {
                    holder.tvDiscountPer.setVisibility(View.VISIBLE);
                    holder.tvDiscount.setVisibility(View.VISIBLE);
                    holder.tvDiscountPer.setText(menuLists.get(position).getDiscount() + "%");
                    double actual_price = Double.parseDouble(price[0]);
                    double disc_per = Double.parseDouble(menuLists.get(position).getDiscount());
                    double disc_amt = (actual_price * disc_per) / 100;
                    double disc_price = actual_price - disc_amt;
                    holder.tvDiscount.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.tvDiscount.setText(SharedPref.getCurrency(activity) + price[0]);
                    holder.tvPrice.setText(SharedPref.getCurrency(activity) + disc_price);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    } //View Holder end here

    private void method(CategoryMenuCustomerAdapter.ViewHolder holder, int position, String qty, int[] var_pos)
    {
        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.et_quantity);

        EditText mInput;
        String image;
        TextView mActionOk;
        CircleImageView mActionCancel;
        TextView name;

        mActionCancel = dialog.findViewById(R.id.action_cancel);
        mActionOk = dialog.findViewById(R.id.action_ok);
        name=dialog.findViewById(R.id.product_name);
        // image=dialog.findViewById(R.id.image);
        mInput = dialog.findViewById(R.id.input);


        name.setText(menuLists.get(position).getName());

        if(qty.equals(""))
        {

        }
        else
        {
            mInput.setText(qty);
        }
        //String qty=mInput.getText().toString();

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String qty=mInput.getText().toString();


                if(qty.equals(""))
                {

                }
                else {

                    holder.tvQty.setText(qty);
                }
                String price = null;
                String[] splited = holder.tvPrice.getText().toString().split(Method.escapeSpecialRegexChars(SharedPref.getCurrency(activity)));
                try {
                    price= splited[1];
                } catch (Exception e) {
                    e.printStackTrace();
                }

                addtocart(holder,menuLists.get(position).getCat_id(),menuLists.get(position).getId(),menuLists.get(position).getName(),menuLists.get(position).getImage(),menuLists.get(position).getVariantLists().get(var_pos[0]).getId(),menuLists.get(position).getVariantLists().get(var_pos[0]).getVolume(),price,menuLists.get(position).getVariantLists().get(var_pos[0]).getVolume_qty(), Constants.INCREMENT);

                dialog.dismiss();

            }
        });

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qty=mInput.getText().toString();

                holder.tvQty.setText(qty);

                dialog.dismiss();

            }
        });


        dialog.show();


    }




    @Override
    public int getItemCount() {
        return menuLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgMenu, imgDesc, imgIncr;
        private TextView tvName, tvDesc, tvPrice, tvQty, tvDiscount, tvDiscountPer;
        LinearLayout linearLayout;
        Spinner spinner_variant;

        public ViewHolder(View itemView) {
            super(itemView);
            imgMenu = (ImageView) itemView.findViewById(R.id.imgMenu);
            //  imgDesc = (ImageView) itemView.findViewById(R.id.imgDesc);
            //  imgIncr = (ImageView) itemView.findViewById(R.id.imgIncr);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvQty = (TextView) itemView.findViewById(R.id.tvQty);
            tvDiscount = (TextView) itemView.findViewById(R.id.tvDiscount);
            tvDiscountPer = (TextView) itemView.findViewById(R.id.tvDiscountPer);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout);
            spinner_variant = (Spinner) itemView.findViewById(R.id.spinner_variant);

        }
    }

    private void addtocart(CategoryMenuCustomerAdapter.ViewHolder holder,final String cat_id, final String id, final String name, final String image, final String variant_id, final String volume, final String price, final String volume_qty, final String operation) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.add_cart,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Response
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("MANAGE_CART");
                            String message = jsonObject1.getString("message");
                            CategoryDetailActivity detailActivity = (CategoryDetailActivity) activity;
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
                })
        {
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
                params.put("variant_qty", holder.tvQty.getText().toString());
                params.put("volume_qty", volume_qty);
                params.put("operation", operation);
                return params;
            }
        };
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

}
