package com.aaijee.app.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaijee.app.Activity.CartActivity;
import com.aaijee.app.Model.Cart;
import com.aaijee.app.R;
import com.aaijee.app.Util.Constants;
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
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Activity activity;
    ArrayList<Cart> carts;

    public CartAdapter(Activity activity, ArrayList<Cart> carts) {
        this.activity = activity;
        this.carts = carts;
    }

    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.cart_item, parent, false);

        return new CartAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CartAdapter.ViewHolder holder, final int position) {
        final Cart cart=carts.get(position);
        Glide.with(activity).load(carts.get(position).getMenu_image()).thumbnail(Glide.with(activity).load(R.drawable.loading)).centerCrop().into(holder.imgMenu);
        holder.tvMenu.setText(carts.get(position).getMenu_name()+" ("+carts.get(position).getVolume_qty()+carts.get(position).getVariant_volume_type()+")");
        holder.tvPrice.setText(SharedPref.getCurrency(activity)+" "+carts.get(position).getVariant_price());
        holder.tvQty.setText(carts.get(position).getVariant_qty());

        GradientDrawable gd = new GradientDrawable();
        if (SharedPref.getDASHED(activity).equalsIgnoreCase("1")) {
            gd.setColor(activity.getResources().getColor(R.color.colorGrey));
            gd.setCornerRadius(25);
            gd.setStroke(4, activity.getResources().getColor(R.color.colorGrey1), 12, 16);
        }else{
            gd.setColor(activity.getResources().getColor(R.color.colorGrey));
            gd.setCornerRadius(25);
            gd.setStroke(4, activity.getResources().getColor(R.color.colorGrey1));
        }
     //   holder.rel_layout.setBackground(gd);

        holder.tvQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method(holder,position,holder.tvQty.getText().toString(),cart);
            }
        });

//        holder.tvPlus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addtocart(cart.getCategory_id(),cart.getMenu_id(),cart.getMenu_name(),cart.getMenu_image(),cart.getVariant_id(),cart.getVariant_volume_type(),cart.getVariant_price(),cart.getVolume_qty(),Constants.INCREMENT);
//            }
//        });
//
//        holder.tvMinus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addtocart(cart.getCategory_id(),cart.getMenu_id(),cart.getMenu_name(),cart.getMenu_image(),cart.getVariant_id(),cart.getVariant_volume_type(),cart.getVariant_price(),cart.getVolume_qty(),Constants.DECREMENT);
//            }
//        });

        if (carts.get(position).getToppings().size()>0){
            holder.extra_layout.setVisibility(View.VISIBLE);
            holder.recycler_extra.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
            holder.recycler_extra.setAdapter(new ToppingAdapter(activity,carts.get(position).getToppings()));
        }else{
            holder.extra_layout.setVisibility(View.GONE);
        }

    }

    private void method(ViewHolder holder, int position, String qty, Cart cart)
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


        name.setText(cart.getMenu_name());

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

              addtocart(holder,cart.getCategory_id(),cart.getMenu_id(),cart.getMenu_name(),cart.getMenu_image(),cart.getVariant_id(),cart.getVariant_volume_type(),cart.getVariant_price(),cart.getVolume_qty(),Constants.INCREMENT);

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
        return carts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imgMenu;
        TextView tvMenu,tvPrice,tvQty;
        ImageView tvPlus,tvMinus;
        RecyclerView recycler_extra;
        RelativeLayout extra_layout,rel_layout;

        public ViewHolder(View itemView) {
            super(itemView);

            imgMenu = (CircleImageView) itemView.findViewById(R.id.imgMenu);
            tvMenu = (TextView) itemView.findViewById(R.id.tvMenu);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvQty = (TextView) itemView.findViewById(R.id.tvQty);
//            tvPlus = (ImageView) itemView.findViewById(R.id.tvPlus);
//            tvMinus = (ImageView) itemView.findViewById(R.id.tvMinus);
            recycler_extra = (RecyclerView) itemView.findViewById(R.id.recycler_extra);
            extra_layout = (RelativeLayout) itemView.findViewById(R.id.extra_layout);
            rel_layout = (RelativeLayout) itemView.findViewById(R.id.rel_layout);

        }
    }

    private void addtocart(ViewHolder holder, final String cat_id, final String id, final String name, final String image, final String variant_id, final String volume, final String price, final String volume_qty, final String operation) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.add_cart,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            JSONArray jsonArray = jsonObject.getJSONArray("MANAGE_CART");
//                            for (int i=0;i<jsonArray.length();i++) {
//                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                                String message = jsonObject1.getString("message");
//                                CartActivity cartActivity = (CartActivity) activity;
//                                cartActivity.getCartList();
//                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//                            }
//                        }

                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("MANAGE_CART");
                            String message = jsonObject1.getString("message");
                            CartActivity cartActivity = (CartActivity) activity;
                            cartActivity.getCartList();
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Error
                        Log.d("AddToCart->",error.toString());
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