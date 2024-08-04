package com.aaijee.app.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
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

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aaijee.app.Activity.MainActivity;
import com.aaijee.app.Activity.MenuDetailActivity;
import com.aaijee.app.Fragment.HomeFragment;
import com.aaijee.app.Model.HomeMenuList;
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
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeMenuAdapter extends RecyclerView.Adapter<HomeMenuAdapter.ViewHolder> {

    private Activity activity;
    ArrayList<HomeMenuList> homeMenuLists;
    String cat_id;
    HomeFragment fragment;
    String endtime ="";
    String starttime ="";
    TextView tvCartCount;
    private final int limit = 6;
    String mInput;

    public HomeMenuAdapter(Activity activity, ArrayList<HomeMenuList> homeMenuLists, String cat_id, HomeFragment fragment)
    {
        this.activity = activity;
        this.homeMenuLists = homeMenuLists;
        this.cat_id = cat_id;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.home_menu_tem_demo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        Glide.with(activity).load(homeMenuLists.get(position).getImage()).thumbnail(Glide.with(activity).load(R.drawable.loading)).centerCrop().into(holder.imgCategory);

//        if (homeMenuLists.get(position).getVariantLists().get(0).getStatus().equalsIgnoreCase("false"))
//        {
//            holder.tvPlus.setVisibility(View.VISIBLE);
//            holder.tvMinus.setVisibility(View.GONE);
//        }else{
//            holder.tvPlus.setVisibility(View.GONE);
//            holder.tvMinus.setVisibility(View.VISIBLE);
//        }

        String qty = homeMenuLists.get(position).getVariantLists().get(0).getQty();

        if (!qty.equals("0"))
        {
            holder.tvAddtoCart.setVisibility(View.GONE);
            holder.r_addCart.setVisibility(View.VISIBLE);
            holder.count.setText(qty);
        }
        else
        {
            holder.tvAddtoCart.setVisibility(View.VISIBLE);
            holder.r_addCart.setVisibility(View.GONE);
        }

        if(holder.count.equals("0"))
        {
            holder.tvAddtoCart.setVisibility(View.VISIBLE);
            holder.r_addCart.setVisibility(View.GONE);
        }

        if (homeMenuLists.get(position).getDiscount().equalsIgnoreCase("0"))
        {
            holder.tvDiscountPrice.setVisibility(View.GONE);
            holder.tvDiscount.setVisibility(View.GONE);
            holder.tvPrice.setText(SharedPref.getCurrency(activity)+homeMenuLists.get(position).getVariantLists().get(0).getPrice());
        }
        else
        {
            holder.tvDiscountPrice.setVisibility(View.VISIBLE);
            holder.tvDiscount.setVisibility(View.VISIBLE);
            holder.tvDiscountPrice.setText(homeMenuLists.get(position).getDiscount()+"%");
            double actual_price = Double.parseDouble(homeMenuLists.get(position).getVariantLists().get(0).getPrice());
            double disc_per = Double.parseDouble(homeMenuLists.get(position).getDiscount());
            double disc_amt = (actual_price*disc_per)/100;
            double disc_price = actual_price-disc_amt;

            holder.tvDiscount.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvDiscount.setText(SharedPref.getCurrency(activity)+homeMenuLists.get(position).getVariantLists().get(0).getPrice());
            holder.tvPrice.setText(SharedPref.getCurrency(activity));
        }

//        GradientDrawable gd = new GradientDrawable();
//        if (SharedPref.getDASHED(activity).equalsIgnoreCase("1"))
//        {
//            gd.setColor(Color.WHITE);
//            gd.setCornerRadius(25);
//            gd.setStroke(4, activity.getResources().getColor(R.color.colorGrey1), 12, 16);
//        }
//        else
//        {
//            gd.setColor(Color.WHITE);
//            gd.setCornerRadius(25);
//            gd.setStroke(4, activity.getResources().getColor(R.color.colorGrey1));
//        }
//        holder.cat_lay.setBackground(gd);

        holder.tvCategory.setText(homeMenuLists.get(position).getName());

        holder.cat_lay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(activity, MenuDetailActivity.class);
                intent.putExtra("menu_image",homeMenuLists.get(position).getImage());
                intent.putExtra("menu_name",homeMenuLists.get(position).getName());
                intent.putExtra("menu_id",homeMenuLists.get(position).getId());
                intent.putExtra("cat_id",homeMenuLists.get(position).getCat_id());
                intent.putExtra("qty",holder.count.getText().toString());
                activity.startActivity(intent);
            }
        });




//        holder.tvPlus.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v) {
//                String start_time = homeMenuLists.get(position).getFood_opening_time();
//                String end_time = homeMenuLists.get(position).getFood_closing_time();
//                if (!start_time.equalsIgnoreCase("") && !start_time.equalsIgnoreCase("0") && start_time!=null) {
//                    try {
//                        Date time1 = new SimpleDateFormat("HH:mm").parse(start_time);
//                        Calendar calendar1 = Calendar.getInstance();
//                        calendar1.setTime(time1);
//                        calendar1.add(Calendar.DATE, 1);
//
//                        Date time2 = new SimpleDateFormat("HH:mm").parse(end_time);
//                        Calendar calendar2 = Calendar.getInstance();
//                        calendar2.setTime(time2);
//                        calendar2.add(Calendar.DATE, 1);
//
//                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//                        String str = sdf.format(new Date());
//                        Date d = new SimpleDateFormat("HH:mm").parse(str);
//                        Calendar calendar3 = Calendar.getInstance();
//                        calendar3.setTime(d);
//                        calendar3.add(Calendar.DATE, 1);
//
//                        Log.d("string1->>", time1 + "");
//                        Log.d("string2->>", time2 + "");
//                        Log.d("date->>", d + "");
//                        Date x = calendar3.getTime();
//                        if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
//                            //checkes whether the current time is between 14:49:00 and 20:11:13.
//                            //Add to cart
//                            String price = null;
//
//                            String[] splited = holder.tvPrice.getText().toString().split(Method.escapeSpecialRegexChars(SharedPref.getCurrency(activity)));
//                            try {
//                                price = splited[1];
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            addtocart(holder, cat_id, homeMenuLists.get(position).getId(), homeMenuLists.get(position).getName(), homeMenuLists.get(position).getImage(), homeMenuLists.get(position).getVariantLists().get(0).getId(), homeMenuLists.get(position).getVariantLists().get(0).getVolume(), price, homeMenuLists.get(position).getVariantLists().get(0).getVolume_qty(), Constants.INCREMENT);
//                        }
//                        else
//                            {
//                            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
//                            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
//                            Date _24HourStart = _24HourSDF.parse(homeMenuLists.get(position).getFood_opening_time());
//                            Date _24HourEnd = _24HourSDF.parse(homeMenuLists.get(position).getFood_closing_time());
//                            starttime=_12HourSDF.format(_24HourStart);
//                            endtime=_12HourSDF.format(_24HourEnd);
//                            String msg =  String.format(homeMenuLists.get(position).getFood_time_msg(), homeMenuLists.get(position).getName(), starttime+" - "+endtime);
//                            AlertDialog dialog=new AlertDialog.Builder(activity).setTitle(R.string.app_name).setMessage(msg)
//                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                                        }
//                                    }).show();
//                            TextView textView=dialog.findViewById(android.R.id.message);
//                            Typeface face= null;
//                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                                face = activity.getResources()
//                                        .getFont(R.font.calibri_regular);
//                            }
//                            textView.setTypeface(face);
//                        }
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//                else{
//                    //Add to cart
//                    String price = null;
//
//                    String[] splited = holder.tvPrice.getText().toString().split(Method.escapeSpecialRegexChars(SharedPref.getCurrency(activity)));
//                    try {
//                        price= splited[1];
//                    } catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//                    addtocart(holder,cat_id,homeMenuLists.get(position).getId(),homeMenuLists.get(position).getName(),homeMenuLists.get(position).getImage(),homeMenuLists.get(position).getVariantLists().get(0).getId(),homeMenuLists.get(position).getVariantLists().get(0).getVolume(),price,homeMenuLists.get(position).getVariantLists().get(0).getVolume_qty(),Constants.INCREMENT);
//                }
//            }
//        });
//
//        holder.tvMinus.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                String price = null;
//                String[] splited = holder.tvPrice.getText().toString().split(Method.escapeSpecialRegexChars(SharedPref.getCurrency(activity)));
//                try
//                {
//                    price= splited[1];
//                } catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//
//                addtocart(holder,cat_id,homeMenuLists.get(position).getId(),homeMenuLists.get(position).getName(),homeMenuLists.get(position).getImage(),homeMenuLists.get(position).getVariantLists().get(0).getId(),homeMenuLists.get(position).getVariantLists().get(0).getVolume(),price,homeMenuLists.get(position).getVariantLists().get(0).getVolume_qty(),Constants.DECREMENT);
//            }
//        });



        holder.tvAddtoCart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)

            {

//                String food_opening_time = homeMenuLists.get(position).getFood_opening_time();
//                String food_closing_time = homeMenuLists.get(position).getFood_closing_time();
//                if (!food_opening_time.equalsIgnoreCase("") && !food_opening_time.equalsIgnoreCase("0") && food_opening_time!=null) {
//                    try {
//                        Date time1 = new SimpleDateFormat("HH:mm").parse(food_opening_time);
//                        Calendar calendar1 = Calendar.getInstance();
//                        calendar1.setTime(time1);
//                        calendar1.add(Calendar.DATE, 1);
//
//                        Date time2 = new SimpleDateFormat("HH:mm").parse(food_closing_time);
//                        Calendar calendar2 = Calendar.getInstance();
//                        calendar2.setTime(time2);
//                        calendar2.add(Calendar.DATE, 1);
//
//                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//                        String str = sdf.format(new Date());
//                        Date d = new SimpleDateFormat("HH:mm").parse(str);
//                        Calendar calendar3 = Calendar.getInstance();
//                        calendar3.setTime(d);
//                        calendar3.add(Calendar.DATE, 1);
//
//                        Log.d("string1->>", time1 + "");
//                        Log.d("string2->>", time2 + "");
//                        Log.d("date->>", d + "");
//                        Date x = calendar3.getTime();
//                        if (x.after(calendar1.getTime()) && x.before(calendar2.getTime()))
//                        {
//                            //checkes whether the current time is between 14:49:00 and 20:11:13.
//                            //Add to cart
//                            holder.r_addCart.setVisibility(View.VISIBLE);
//                            holder.tvAddtoCart.setVisibility(View.GONE);
//                            holder.increase.performClick();
//
//                        }
//                        else
//                        {
//                            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
//                            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
//                            Date _24HourStart = _24HourSDF.parse(food_opening_time);
//                            Date _24HourEnd = _24HourSDF.parse(food_closing_time);
//                            starttime=_12HourSDF.format(_24HourStart);
//                            endtime=_12HourSDF.format(_24HourEnd);
//                            String msg =  String.format(homeMenuLists.get(position).getFood_time_msg(), homeMenuLists.get(position).getName(), starttime+" - "+endtime);
//                            AlertDialog dialog=new AlertDialog.Builder(activity).setTitle(R.string.app_name).setMessage(msg)
//                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i)
//                                        {
//
//                                        }
//                                    }).show();
//                            TextView textView=dialog.findViewById(android.R.id.message);
//                            Typeface face= null;
//                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
//                            {
//                                face = activity.getResources()
//                                        .getFont(R.font.calibri_regular);
//                            }
//                            textView.setTypeface(face);
//                        }
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }

                //holder.r_addCart.setVisibility(View.VISIBLE);
              //  holder.rl.setVisibility(View.VISIBLE);


                method(holder,position,holder.count.getText().toString(),homeMenuLists.get(position).getName());
              //  holder.tvAddtoCart.setVisibility(View.GONE);
               // holder.increase.performClick();
            }
        });



//        holder.increase.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
////                String food_opening_time = homeMenuLists.get(position).getFood_opening_time();
////                String food_closing_time = homeMenuLists.get(position).getFood_closing_time();
////                if (!food_opening_time.equalsIgnoreCase("") && !food_opening_time.equalsIgnoreCase("0") && food_opening_time!=null) {
////                    try {
////                        Date time1 = new SimpleDateFormat("HH:mm").parse(food_opening_time);
////                        Calendar calendar1 = Calendar.getInstance();
////                        calendar1.setTime(time1);
////                        calendar1.add(Calendar.DATE, 1);
////
////                        Date time2 = new SimpleDateFormat("HH:mm").parse(food_closing_time);
////                        Calendar calendar2 = Calendar.getInstance();
////                        calendar2.setTime(time2);
////                        calendar2.add(Calendar.DATE, 1);
////
////                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
////                        String str = sdf.format(new Date());
////                        Date d = new SimpleDateFormat("HH:mm").parse(str);
////                        Calendar calendar3 = Calendar.getInstance();
////                        calendar3.setTime(d);
////                        calendar3.add(Calendar.DATE, 1);
////
////                        Log.d("string1->>", time1 + "");
////                        Log.d("string2->>", time2 + "");
////                        Log.d("date->>", d + "");
////                        Date x = calendar3.getTime();
////                        if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
////                            //checkes whether the current time is between 14:49:00 and 20:11:13.
////                            //Add to cart
////                            int item_count = Integer.parseInt(holder.count.getText().toString());
////                            item_count=item_count+1;
////                            holder.count.setText(String.valueOf(item_count));
////                            String price = null;
////                            String[] splited = holder.tvPrice.getText().toString().split(Method.escapeSpecialRegexChars(SharedPref.getCurrency(activity)));
////                            try
////                            {
////                                price= splited[1];
////                            } catch (Exception e) {
////                                e.printStackTrace();
////                            }
////                            addtocart(holder, homeMenuLists.get(position).getCat_id(), homeMenuLists.get(position).getId(), homeMenuLists.get(position).getName(), homeMenuLists.get(position).getImage(), homeMenuLists.get(position).getVariantLists().get(0).getId(), homeMenuLists.get(position).getVariantLists().get(0).getVolume(), price, homeMenuLists.get(position).getVariantLists().get(0).getVolume_qty(), Constants.INCREMENT);
////                        }
////                        else
////                        {
////                            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
////                            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
////                            Date _24HourStart = _24HourSDF.parse(food_opening_time);
////                            Date _24HourEnd = _24HourSDF.parse(food_closing_time);
////                            starttime=_12HourSDF.format(_24HourStart);
////                            endtime=_12HourSDF.format(_24HourEnd);
////                            String msg =  String.format(homeMenuLists.get(position).getFood_time_msg(), homeMenuLists.get(position).getName(), starttime+" - "+endtime);
////                            AlertDialog dialog=new AlertDialog.Builder(activity).setTitle(R.string.app_name).setMessage(msg)
////                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
////                                        @Override
////                                        public void onClick(DialogInterface dialogInterface, int i)
////                                        {
////
////                                        }
////                                    }).show();
////                            TextView textView=dialog.findViewById(android.R.id.message);
////                            Typeface face= null;
////                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
////                                face = activity.getResources()
////                                        .getFont(R.font.calibri_regular);
////                            }
////                            textView.setTypeface(face);
////                        }
////                    } catch (ParseException e) {
////                        e.printStackTrace();
////                    }
////                }else
////                    {
//                    //Add to cart
////                    int item_count = Integer.parseInt(holder.count.getText().toString());
////                    item_count=item_count+1;
////                    holder.count.setText(String.valueOf(item_count));
////                    String price = null;
////                    String[] splited = holder.tvPrice.getText().toString().split(Method.escapeSpecialRegexChars(SharedPref.getCurrency(activity)));
////                    try
////                    {
////                        price= splited[1];
////                    } catch (Exception e)
////                    {
////                        e.printStackTrace();
////                    }
////                    addtocart(holder, homeMenuLists.get(position).getCat_id(), homeMenuLists.get(position).getId(), homeMenuLists.get(position).getName(), homeMenuLists.get(position).getImage(), homeMenuLists.get(position).getVariantLists().get(0).getId(), homeMenuLists.get(position).getVariantLists().get(0).getVolume(), price, homeMenuLists.get(position).getVariantLists().get(0).getVolume_qty(), Constants.INCREMENT);
////
////               // }
//
//            }
//        });


        holder.count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method(holder,position,holder.count.getText().toString(),homeMenuLists.get(position).getName());
            }
        });

//        holder.decrease.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                int item_count = Integer.parseInt(holder.count.getText().toString());
//
//                if (item_count ==1)
//                {
//
//                    //holder.count.setText(0);
//                    item_count = item_count - 1;
//                    holder.count.setText(String.valueOf(item_count));
//                    String price = null;
//                    String[] splited = holder.tvPrice.getText().toString().split(Method.escapeSpecialRegexChars(SharedPref.getCurrency(activity)));
//                    try
//                    {
//                        price= splited[1];
//                    }
//                    catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//                    addtocart(holder,homeMenuLists.get(position).getCat_id(),homeMenuLists.get(position).getId(),homeMenuLists.get(position).getName(),homeMenuLists.get(position).getImage(),homeMenuLists.get(position).getVariantLists().get(0).getId(),homeMenuLists.get(position).getVariantLists().get(0).getVolume(),price,homeMenuLists.get(position).getVariantLists().get(0).getVolume_qty(),Constants.DECREMENT);
//
//                    holder.r_addCart.setVisibility(View.GONE);
//                    holder.tvAddtoCart.setVisibility(View.VISIBLE);
//
//
//                }
//                else
//                {
//                    item_count = item_count - 1;
//                    holder.count.setText(String.valueOf(item_count));
//                    String price = null;
//                    String[] splited = holder.tvPrice.getText().toString().split(Method.escapeSpecialRegexChars(SharedPref.getCurrency(activity)));
//                    try
//                    {
//                        price= splited[1];
//                    }
//
//                    catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//                    addtocart(holder,homeMenuLists.get(position).getCat_id(),homeMenuLists.get(position).getId(),homeMenuLists.get(position).getName(),homeMenuLists.get(position).getImage(),homeMenuLists.get(position).getVariantLists().get(0).getId(),homeMenuLists.get(position).getVariantLists().get(0).getVolume(),price,homeMenuLists.get(position).getVariantLists().get(0).getVolume_qty(),Constants.DECREMENT);
//
//////                   tvCartCount.setVisibility(View.GONE);
////                    MainActivity mainActivity = (MainActivity) activity;
////                    mainActivity.cartcount();
//                }
//            }
//        });

    }

    private void method(ViewHolder holder, int position,String qty,String product_name)
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


        name.setText(product_name);

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
                holder.r_addCart.setVisibility(View.VISIBLE);


                if(qty.equals(""))
                {
                    holder.tvAddtoCart.setVisibility(View.VISIBLE);

                }
                else {
                    holder.tvAddtoCart.setVisibility(View.GONE);
                    holder.count.setText(qty);
                }
                String price = null;
                String[] splited = holder.tvPrice.getText().toString().split(Method.escapeSpecialRegexChars(SharedPref.getCurrency(activity)));
                try
                {
                    price= splited[1];
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                addtocart(holder, homeMenuLists.get(position).getCat_id(), homeMenuLists.get(position).getId(), homeMenuLists.get(position).getName(), homeMenuLists.get(position).getImage(), homeMenuLists.get(position).getVariantLists().get(0).getId(), homeMenuLists.get(position).getVariantLists().get(0).getVolume(), price, homeMenuLists.get(position).getVariantLists().get(0).getVolume_qty(), Constants.INCREMENT);

                dialog.dismiss();

            }
        });

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qty=mInput.getText().toString();

//                holder.r_addCart.setVisibility(View.VISIBLE);
//                if(qty.equals(""))
//                {
//                    holder.tvAddtoCart.setVisibility(View.VISIBLE);
//                    holder.r_addCart.setVisibility(View.GONE);
//                }
//                else {
//                    holder.count.setText(qty);
//                }
                dialog.dismiss();

            }
        });


        dialog.show();


    }

    private void addtocart(final ViewHolder holder, final String cat_id, final String id, final String name, final String image, final String variant_id, final String volume, final String price, final String volume_qty, final String operation) {

        //Log.d("TAG", price.toString());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.add_cart,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        //Response
                        Log.d("TAG", "onResponse: "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("TAG1", "onResponse: "+response);

                            JSONObject jsonObject1 = jsonObject.getJSONObject("MANAGE_CART");

                            String message = jsonObject1.getString("message");

                            MainActivity mainActivity = (MainActivity) activity;
                            mainActivity.cartcount();


                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            //Toast.makeText(activity, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //Error
                        Log.d("AddToCart->",error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", SharedPref.getUserId(activity));
                params.put("category_id", cat_id);
                params.put("menu_id", id);
                params.put("menu_name", name);
                params.put("menu_image", image);
                params.put("variant_id", variant_id);
                params.put("variant_type", volume);
                params.put("variant_price", price);
                params.put("variant_qty", holder.count.getText().toString());
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



//    private void openDialog(final String t_name, final String t_price) {
//        final Dialog dialog = new Dialog(activity);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.et_quantity);
//
//        EditText etQuantity =dialog.findViewById(R.id.u_quantity);
//        Button submit =dialog.findViewById(R.id.tvSubmit);
//        // btnContinue.setText("Pay"+" "+t_price);
//
//
//
//        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });
//
//        dialog.show();
//    }


    @Override
    public int getItemCount() {

        //return homeMenuLists.size();
        if(homeMenuLists.size() > limit){
            return limit;
        }
        else
        {
            return homeMenuLists.size();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCategory;
        TextView tvCategory,tvPrice, tvPlus, tvMinus, tvDiscountPrice, tvDiscount,ok,count,product_name;
        RelativeLayout cat_lay,rl;
        CardView r_addCart,tvAddtoCart;
        EditText input;
        ImageView increase,decrease,cancel;

        public ViewHolder(View itemView) {
            super(itemView);

            imgCategory = (ImageView) itemView.findViewById(R.id.imgCategory);
            tvCategory = (TextView) itemView.findViewById(R.id.tvCategory);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
//           tvPlus = (TextView) itemView.findViewById(R.id.tvPlus);
//           tvMinus = (TextView) itemView.findViewById(R.id.tvMinus);
            //increase = (ImageView) itemView.findViewById(R.id.increase);
           // decrease = (ImageView) itemView.findViewById(R.id.decrease);
            tvDiscountPrice = (TextView) itemView.findViewById(R.id.tvDiscountPrice);
            tvDiscount = (TextView) itemView.findViewById(R.id.tvDiscount);
            cat_lay = (RelativeLayout) itemView.findViewById(R.id.cat_lay);
            tvAddtoCart =(CardView) itemView.findViewById(R.id.tvAddtoCart);
            r_addCart=(CardView) itemView.findViewById(R.id.r_addcart);
//            rl=(RelativeLayout) itemView.findViewById(R.id.rl);

            ok = (TextView) itemView.findViewById(R.id.action_ok);
            cancel =  itemView.findViewById(R.id.action_cancel);
            count=(TextView) itemView.findViewById(R.id.count);
            input=(EditText) itemView.findViewById(R.id.input);
//          tvCartCount=itemView.findViewById(R.id.tvCartCount);


        }
    }

}