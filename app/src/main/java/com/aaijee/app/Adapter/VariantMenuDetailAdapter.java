package com.aaijee.app.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.aaijee.app.Activity.MenuDetailActivity;
import com.aaijee.app.Model.MenuList;
import com.aaijee.app.Model.VariantList;
import com.aaijee.app.R;
import com.aaijee.app.Util.Constants;
import com.aaijee.app.Util.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class VariantMenuDetailAdapter extends RecyclerView.Adapter<VariantMenuDetailAdapter.ViewHolder> {

    private Activity activity;
    ArrayList<VariantList> variantLists;
    MenuList menuList;
    private long mLastClick = 0;
    String flavour;
    String flavour_name;
    String flavourId;
    String flavour_Id;


    public VariantMenuDetailAdapter(Activity activity, MenuList menuList, ArrayList<VariantList> variantLists) {
        this.activity = activity;
        this.menuList = menuList;
        this.variantLists = variantLists;

    }

    @Override
    public VariantMenuDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.menu_detail_variant, parent, false);

        return new VariantMenuDetailAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final VariantMenuDetailAdapter.ViewHolder holder, final int position) {

        holder.tvVariant.setText(variantLists.get(position).getVolume());
        holder.tvPrice.setText(SharedPref.getCurrency(activity)+variantLists.get(position).getPrice());
        Glide.with(activity).load(menuList.getImage()).thumbnail(Glide.with(activity).load(R.drawable.loading)).into(holder.image_food);

        holder.tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (variantLists.get(position).getSubMenuLists().size()>0) {

                }

//                String start_time = menuList.getFood_opening_time();
//                String end_time = menuList.getFood_closing_time();
//
//
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
//                            int qty = Integer.parseInt(holder.tvQty.getText().toString());
//                            holder.tvQty.setText(String.valueOf(qty+1));
//                            final Dialog dialog = new Dialog(activity);
//
//                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                            dialog.setCancelable(false);
//
//                            dialog.setContentView(R.layout.flavour_list_dialog);
//                            final Button submit = dialog.findViewById(R.id.button_submit);
//                            final ProgressBar progress_order = dialog.findViewById(R.id.progress_order);
//                            final RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
//                            final ArrayList flavourList = new ArrayList<>();
//                            progress_order.setVisibility(View.VISIBLE);
//
//                            if (menuList.getFlavourLists().size() > 0) {
//
//                                progress_order.setVisibility(View.GONE);
//                                submit.setEnabled(true);
//                                dialog.show();
//
//                            } else {
//                                String f_name = "", f_id = "";
//
//                                addtocart(menuList.getCat_id(), menuList.getId(), menuList.getName(), menuList.getImage(), variantLists.get(position).getId(), variantLists.get(position).getVolume(), variantLists.get(position).getPrice(), Constants.DECREMENT,f_id,f_name);
//                            }
//
//
//                            ImageView button_close = dialog.findViewById(R.id.button_close);
//
//                            button_close.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                    dialog.dismiss();
//
//                                }
//                            });
//
//                            radioGroup.removeAllViews();
//                            radioGroup.setOrientation(LinearLayout.VERTICAL);
//                            for (int i = 0; i < menuList.getFlavourLists().size(); i++) {
//                                final RadioButton rbn = new RadioButton(activity);
//                                rbn.setId(View.generateViewId());
//                                rbn.setPadding(10, 10, 10, 10);
//                                rbn.setTextSize(16);
//                                rbn.setText(menuList.getFlavourLists().get(i).getFlavour_name());
//                                rbn.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        flavour = String.valueOf(((RadioButton) v).getText());
//                                        flavourId = String.valueOf(rbn.getId());
//
//                                        for (int j = 0; j < menuList.getFlavourLists().size(); j++) {
//                                            if (menuList.getFlavourLists().get(j).getFlavour_name().equalsIgnoreCase(flavour)) {
//                                                flavour_Id = menuList.getFlavourLists().get(j).getF_id();
//                                                flavour_name = menuList.getFlavourLists().get(j).getFlavour_name();
//                                            }
//                                        }
//                                    }
//                                });
//                                radioGroup.addView(rbn);
//                            }
//
//                            submit.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    if (flavour_Id != null && Integer.parseInt(flavour_Id) > 0) {
//
//                                        if (SystemClock.elapsedRealtime() - mLastClick < 1000) {
//
//                                            return;
//                                        }
//
//                                        mLastClick = SystemClock.elapsedRealtime();
//
//                                        addtocart(menuList.getCat_id(),menuList.getId(),menuList.getName(),menuList.getImage(),variantLists.get(position).getId(),variantLists.get(position).getVolume(),variantLists.get(position).getPrice(),Constants.INCREMENT,flavour_Id,flavour_name);
//                                        dialog.dismiss();
//                                    }
//                                }
//                            });
//
//                        } else {
//                            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
//                            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
//                            Date _24HourStart = _24HourSDF.parse(menuList.getFood_opening_time());
//                            Date _24HourEnd = _24HourSDF.parse(menuList.getFood_closing_time());
//                            String starttime = _12HourSDF.format(_24HourStart);
//                            String endtime = _12HourSDF.format(_24HourEnd);
//                            String msg =  String.format(menuList.getFood_time_msg(), menuList.getName(), starttime+" - "+endtime);
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
//                }else{
                    //Add to cart
                    int qty = Integer.parseInt(holder.tvQty.getText().toString());
                    holder.tvQty.setText(String.valueOf(qty+1));

                    //add to cart or update cart
                    String f_name = "", f_id = "";

                    addtocart(menuList.getCat_id(), menuList.getId(), menuList.getName(), menuList.getImage(), variantLists.get(position).getId(), variantLists.get(position).getVolume(), variantLists.get(position).getPrice(), Constants.DECREMENT,f_id,f_name);
               // }
            }
        });

        holder.tvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String f_id = "", f_name = "";
                if (variantLists.get(position).getSubMenuLists().size() > 0) {

                } else {

                }
                int qty = Integer.parseInt(holder.tvQty.getText().toString());
                if (qty == 0) {
                    holder.tvQty.setText(String.valueOf(qty - 1));

                    //add to cart or update cart
                    addtocart(menuList.getCat_id(), menuList.getId(), menuList.getName(), menuList.getImage(), variantLists.get(position).getId(), variantLists.get(position).getVolume(), variantLists.get(position).getPrice(), Constants.DECREMENT, f_id, f_name);
                }
            }
        });

        holder.tvQty.setText(variantLists.get(position).getQty());
    }

    private void addtocart(final String cat_id, final String id, final String name, final String image, final String variant_id, final String volume, final String price, final String operation, final String flavour_Id, final String flavour) {

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
                params.put("operation", operation);
                params.put("flavour_id", flavour_Id);
                params.put("flavour", flavour);
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
        return variantLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgBanner;
        CircleImageView image_food;
        TextView tvVariant,tvPrice,tvMinus,tvPlus,tvQty;

        public ViewHolder(View itemView) {
            super(itemView);

            imgBanner = (ImageView) itemView.findViewById(R.id.imgBanner);
            image_food = (CircleImageView) itemView.findViewById(R.id.image_food);
            tvVariant = (TextView) itemView.findViewById(R.id.tvVariant);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvMinus = (TextView) itemView.findViewById(R.id.tvMinus);
            tvPlus = (TextView) itemView.findViewById(R.id.tvPlus);
            tvQty = (TextView) itemView.findViewById(R.id.tvQty);

        }
    }
}