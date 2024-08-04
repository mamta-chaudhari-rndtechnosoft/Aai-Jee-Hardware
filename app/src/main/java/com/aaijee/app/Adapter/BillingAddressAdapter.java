package com.aaijee.app.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaijee.app.Activity.AddressActivity;
import com.aaijee.app.Activity.CreateBillAddressActivity;
import com.aaijee.app.Model.Address;
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

public class BillingAddressAdapter extends RecyclerView.Adapter
{
    public ArrayList<Address> arrayList;
    Activity context;

    private class MyViewHolder extends RecyclerView.ViewHolder
    {

        RelativeLayout relimage;
        ImageView place_image,remove_location;
        LinearLayout edit_location;
        TextView location_display_name,location_display_address,mobile;
        RelativeLayout saved_location_root_view;

        private MyViewHolder(View view) {
            super(view);
            relimage = view.findViewById(R.id.saved_location_tag_background);
            remove_location = view.findViewById(R.id.remove_location);
            place_image = view.findViewById(R.id.saved_location_tag_icon);
            location_display_name = view.findViewById(R.id.saved_location_location_tag_name);
            location_display_address = view.findViewById(R.id.saved_location_address_line);
            edit_location = view.findViewById(R.id.edit_saved_location_view);
            mobile=view.findViewById(R.id.mobile_no);
            saved_location_root_view = view.findViewById(R.id.saved_location_root_view);

        }
    }

    public BillingAddressAdapter(Activity activity, ArrayList<Address> arrayList)
    {
        this.arrayList = arrayList;
        this.context=activity;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_list, parent, false);
        return new BillingAddressAdapter.MyViewHolder(itemView);
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        String add_type = arrayList.get(position).getAddress_type();
        if (add_type.equals("home"))
        {
            Glide.with(context).load(R.drawable.saved_location_home_icon).into(((BillingAddressAdapter.MyViewHolder) holder).place_image);
            ((BillingAddressAdapter.MyViewHolder) holder).relimage.setBackgroundColor(context.getResources().getColor(R.color.saved_location_home_tag_background));
            ((BillingAddressAdapter.MyViewHolder) holder).location_display_name.setTextColor(R.color.saved_location_home_tag_background);
        }
        else if (add_type.equals("office"))
        {
            Glide.with(context).load(R.drawable.saved_location_office_icon).into(((BillingAddressAdapter.MyViewHolder) holder).place_image);
            ((BillingAddressAdapter.MyViewHolder) holder).relimage.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));

        }
        else
        {
            Glide.with(context).load(R.drawable.saved_location_other_location_icon).into(((BillingAddressAdapter.MyViewHolder) holder).place_image);
            ((BillingAddressAdapter.MyViewHolder) holder).relimage.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
        ((BillingAddressAdapter.MyViewHolder) holder).location_display_name.setText(arrayList.get(position).getName());
        ((BillingAddressAdapter.MyViewHolder) holder).location_display_name.setTextSize(18);


        ((BillingAddressAdapter.MyViewHolder) holder).location_display_address.setText(arrayList.get(position).getFlat_no()+","
                +arrayList.get(position).getBuilding_name()+",\n"+arrayList.get(position).getLandmark()+", "
                +arrayList.get(position).getCity());
        ((BillingAddressAdapter.MyViewHolder) holder).location_display_address.setTextSize(12);


        ((BillingAddressAdapter.MyViewHolder) holder).mobile.setText(arrayList.get(position).getMobile());
        ((BillingAddressAdapter.MyViewHolder) holder).mobile.setTextSize(14);



        ((BillingAddressAdapter.MyViewHolder) holder).remove_location.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Delete Address");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                       // deleteaddress(arrayList.get(position).getBilling_address_id());
                    }
                });
                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();

            }
        });

        ((BillingAddressAdapter.MyViewHolder)holder).saved_location_root_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent();
                intent.putExtra("address",((BillingAddressAdapter.MyViewHolder) holder).location_display_address.getText().toString());
              //  intent.putExtra("address_id",arrayList.get(position).getBilling_address_id());
                intent.putExtra("delivery_amount",arrayList.get(position).getDelivery());
               // setPrimary(arrayList.get(position).getBilling_address_id());
              //  context.setResult(Constants.CHOOSE_BILL_ADDRESS_CODE,intent);
                context.finish();
            }
        });
        ((BillingAddressAdapter.MyViewHolder) holder).edit_location.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                context.startActivity(new Intent(context, CreateBillAddressActivity.class).putExtra("type","edit")
                     //   .putExtra("id",arrayList.get(position).getBilling_address_id())
                        .putExtra("user_id",arrayList.get(position).getUser_id())
                        .putExtra("flat_no",arrayList.get(position).getFlat_no())
                        .putExtra("building_name",arrayList.get(position).getBuilding_name())
                        .putExtra("landmark",arrayList.get(position).getLandmark())
                        .putExtra("city",arrayList.get(position).getCity())
                        .putExtra("pincode",arrayList.get(position).getPincode())
                        .putExtra("name",arrayList.get(position).getName())
                        .putExtra("email",arrayList.get(position).getEmail())
                        .putExtra("mbl",arrayList.get(position).getMobile())
                        .putExtra("area_name",arrayList.get(position).getArea_name())
                        .putExtra("gst",arrayList.get(position).getGst())
                        .putExtra("address_type",arrayList.get(position).getAddress_type()));


            }
        });
    }

    private void setPrimary(String id)
    {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.primary_address + "&user_id="+ SharedPref.getUserId(context) +"&primary_address="+ id, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                requestQueue.stop();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void deleteaddress(String id)
    {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.delete_address +"="+ id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("ORDER_BILL_ADDRESS_DELETE");
                    String error=jsonObject1.getString("error");
                    if (error.equalsIgnoreCase("false"))
                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Unable to delete", Toast.LENGTH_SHORT).show();

                    ((AddressActivity)context).newAddress();
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


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }


}
