package com.aaijee.app.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
 

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CreateBillAddressActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private ArrayList<Address> addressArrayList;
    private Address address;
    private Button btnAdd;
    private TextView txthome, txtoffice, txtothers;
    private EditText edtname,edtmbl,edtemail,edtFlat,edtBuilding,edtlandmark,edtcity,edtpincode,edtGstNo;
    private String type;
    private String billing_address_id,user_id,flat_no,building_name,landmark,city,pincode,name,email,mbl,address_type,area_name,gst;
    String params,url,area_id;
    Spinner spinner;
    private String fullname,gst_no;
    int selected_area;
    private String phone;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressBar progress;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bill_address);

        Bundle extras=getIntent().getExtras();
        if(extras == null) {
            type= null;
        } else {
            type= extras.getString("type");
            if (type.equals("edit"))
            {
                billing_address_id = extras.getString("billing_address_id");
                user_id = extras.getString("user_id");
                flat_no = extras.getString("flat_no");
                building_name = extras.getString("building_name");
                landmark = extras.getString("landmark");
                city = extras.getString("city");
                pincode = extras.getString("pincode");
                name = extras.getString("name");
                email = extras.getString("email");
                mbl = extras.getString("mbl");
                area_name = extras.getString("area_name");
                gst=extras.getString("gst");
                address_type = extras.getString("address_type");


                Toast.makeText(getApplicationContext(), "1"+gst, Toast.LENGTH_SHORT).show();
            }
        }

        toolbar=findViewById(R.id.toolbar_create);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
//        if (type.equals("edit")) {
//            toolbar.setTitle(Html.fromHtml("<b>"+getResources().getString(R.string.editadd)+"</b>"));
//        }else {
//            toolbar.setTitle(Html.fromHtml("<b>"+getResources().getString(R.string.newadd)+"</b>"));
//        }
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);

        addressArrayList=new ArrayList<>();

        Constants.area_name.clear();
        Constants.area_name = new ArrayList<>();
        for (int i=0; i<Constants.areaLists.size(); i++){
            Constants.area_name.add(Constants.areaLists.get(i).getName());
            if (area_name!=null && area_name.equals(Constants.areaLists.get(i).getName())){
                selected_area = i;
            }
        }

        spinner = findViewById(R.id.spinner_area);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.area_name);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(selected_area);
        edtname = findViewById(R.id.edtname);
        edtmbl = findViewById(R.id.edtmbl);
        edtemail = findViewById(R.id.edtemail);
        edtFlat = findViewById(R.id.edtFlat);
        edtBuilding = findViewById(R.id.edtBuilding);
        edtlandmark = findViewById(R.id.edtlandmark);
        edtcity = findViewById(R.id.edtcity);
        edtpincode = findViewById(R.id.edtpincode);
        edtGstNo=findViewById(R.id.edtgst);
        btnAdd=findViewById(R.id.btnAdd);
        // txthome=findViewById(R.id.txthome);
        txtoffice=findViewById(R.id.txtoffice);
        txtothers=findViewById(R.id.txtothers);
        progress=findViewById(R.id.progress);
        progress.setVisibility(View.GONE);


        if (type!=null && type.equals("edit")) {
            edtname.setText(name);
            edtmbl.setText(mbl);
            edtemail.setText(email);
            edtFlat.setText(flat_no);
            edtBuilding.setText(building_name);
            edtlandmark.setText(landmark);
            edtcity.setText(city);
            edtpincode.setText(pincode);
            edtGstNo.setText(gst);



//            if (address_type.equals("home")){
//                txthome.setBackground(getResources().getDrawable(R.drawable.rounded_btn));
//                txtoffice.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
//                txtothers.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
//                txthome.setTextColor(getResources().getColor(R.color.colorWhite));
//                txtoffice.setTextColor(getResources().getColor(R.color.colorBlack));
//                txtothers.setTextColor(getResources().getColor(R.color.colorBlack));
//                address_type="home";
//            }
            if (address_type.equals("office")){
                txtoffice.setBackground(getResources().getDrawable(R.drawable.rounded_btn));
                //txthome.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
                txtothers.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
                txtoffice.setTextColor(getResources().getColor(R.color.colorWhite));
                //txthome.setTextColor(getResources().getColor(R.color.colorBlack));
                txtothers.setTextColor(getResources().getColor(R.color.colorBlack));
                address_type="office";
            }else{
                txtothers.setBackground(getResources().getDrawable(R.drawable.rounded_btn));
                //txthome.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
                txtoffice.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
                txtothers.setTextColor(getResources().getColor(R.color.colorWhite));
                // txthome.setTextColor(getResources().getColor(R.color.colorBlack));
                txtoffice.setTextColor(getResources().getColor(R.color.colorBlack));
                address_type="others";
            }
            btnAdd.setText("Update Address");
        }

//        txthome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                txthome.setBackground(getResources().getDrawable(R.drawable.rounded_btn));
//                txtoffice.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
//                txtothers.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
//                txthome.setTextColor(getResources().getColor(R.color.colorWhite));
//                txtoffice.setTextColor(getResources().getColor(R.color.colorBlack));
//                txtothers.setTextColor(getResources().getColor(R.color.colorBlack));
//                address_type="home";
//            }
//        });

        txtoffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtoffice.setBackground(getResources().getDrawable(R.drawable.rounded_btn));
                // txthome.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
                txtothers.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
                txtoffice.setTextColor(getResources().getColor(R.color.colorWhite));
                // txthome.setTextColor(getResources().getColor(R.color.colorBlack));
                txtothers.setTextColor(getResources().getColor(R.color.colorBlack));
                address_type="office";
            }
        });

        txtothers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtothers.setBackground(getResources().getDrawable(R.drawable.rounded_btn));
                //txthome.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
                txtoffice.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
                txtothers.setTextColor(getResources().getColor(R.color.colorWhite));
                //txthome.setTextColor(getResources().getColor(R.color.colorBlack));
                txtoffice.setTextColor(getResources().getColor(R.color.colorBlack));
                address_type="others";
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String name = edtname.getText().toString();
                String mbl = edtmbl.getText().toString();
                String email = edtemail.getText().toString();
                String flat = edtFlat.getText().toString();
                String building = edtBuilding.getText().toString();
                String landmark = edtlandmark.getText().toString();
                String city = edtcity.getText().toString();
                String pincode = edtpincode.getText().toString();
                String gst=edtGstNo.getText().toString();

                if (!checkFullname()) {
                    edtname.requestFocus();
                }else if (!email.matches(emailPattern)) {
                    Toast.makeText(CreateBillAddressActivity.this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
                    edtemail.requestFocus();
                }else if (!checkPhone()){
                    edtmbl.requestFocus();
                }else if (!checkPincode()){
                    edtpincode.requestFocus();
                }else if (area_id==null && area_id.equalsIgnoreCase("")){
                    Toast.makeText(CreateBillAddressActivity.this, "Please Select Area", Toast.LENGTH_SHORT).show();
                }
//                else if(!checkgst())
//                {
//                    edtGstNo.requestFocus();
//                }


                else
                {
                    newAddress(name, mbl, email, gst,flat, building, landmark, city, pincode);

                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area_id = Constants.areaLists.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public Boolean checkPhone() {

        phone = edtmbl.getText().toString();
        if (phone.length() == 0) {
            edtmbl.setError(getString(R.string.error_field_empty));
            return false;
        }
        if (phone.length()!=10){
            edtmbl.requestFocus();
            Toast.makeText(this, "Phone number not valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        edtmbl.setError(null);

        return true;
    }

    public Boolean checkPincode() {

        pincode = edtpincode.getText().toString();
        if (pincode.length() == 0) {
            edtpincode.setError(getString(R.string.error_field_empty));
            return false;
        }
        if (pincode.length()!=6){
            edtpincode.requestFocus();
            Toast.makeText(this, "Pincode not valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        edtpincode.setError(null);

        return true;
    }

    public Boolean checkFullname() {
        fullname = edtname.getText().toString();
        if (fullname.length() == 0) {
            edtname.setError(getString(R.string.error_field_empty));
            return false;
        }
        if (fullname.length() < 2) {
            edtname.setError(getString(R.string.enter_your_name));
            return false;
        }
        edtname.setError(null);
        return  true;
    }

    public Boolean checkgst() {
        gst_no = edtGstNo.getText().toString();
        if (gst_no.length() == 0) {
            edtGstNo.setError(getString(R.string.error_field_empty));
            return false;
        }
        if (gst_no.length() < 2) {
            edtGstNo.setError(getString(R.string.gst));
            return false;
        }
        edtGstNo.setError(null);
        return  true;
    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void newAddress(String name, String mbl, String email,String gst ,String flat, String building, String landmark, String city, String pincode) {
        progress.setVisibility(View.VISIBLE);
        if (type!=null && type.equals("edit")) {
            params = "&billing_address_id=" + billing_address_id + "&user_id=" + SharedPref.getUserId(CreateBillAddressActivity.this) + "&area=" + area_id + "&flat_no=" + flat + "&building_name=" + building + "&landmark=" + landmark + "&city=" + city + "&pincode=" + pincode + "&name=" + name + "&email=" + email + "&mobile=" + mbl + "&address_type=" + address_type +"&gst="+ gst ;
            url= Constants.edit_address;
        }
        else {
            params = "&user_id=" + SharedPref.getUserId(CreateBillAddressActivity.this) + "&area=" + area_id + "&flat_no=" + flat + "&building_name=" + building + "&landmark=" + landmark + "&city=" + city + "&pincode=" + pincode + "&name=" + name + "&email=" + email + "&mobile=" + mbl + "&address_type=" + address_type +"&gst="+ gst;
            url=Constants.new_address;
        }
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url +params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("TAG", "onResponse: "+response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("BILL_ADDRESS");

                    address= new Address();

                    String error = jsonObject1.getString("error");
                    if (error.equals("false")) {
                        String billing_address_id = jsonObject1.getString("billing_address_id");
                        String user_id = jsonObject1.getString("user_id");
                        String flat_no = jsonObject1.getString("flat_no");
                        String building_name = jsonObject1.getString("building_name");
                        String landmark = jsonObject1.getString("landmark");
                        String city = jsonObject1.getString("city");
                        String pincode = jsonObject1.getString("pincode");
                        String name = jsonObject1.getString("name");
                        String email = jsonObject1.getString("email");
                        String mobile = jsonObject1.getString("mobile");
                        String type = jsonObject1.getString("address_type");
                        String gst=jsonObject1.getString("gst");


                        //address.setBilling_address_id(billing_address_id);
                        address.setUser_id(user_id);
                        address.setFlat_no(flat_no);
                        address.setBuilding_name(building_name);
                        address.setLandmark(landmark);
                        address.setCity(city);
                        address.setName(name);
                        address.setPincode(pincode);
                        address.setEmail(email);
                        address.setMobile(mobile);
                        address.setAddress_type(type);
                        address.setGst(gst);

                        //Toast.makeText(CreateBillAddressActivity.this, "", Toast.LENGTH_SHORT).show();


                        progress.setVisibility(View.GONE);
                        addressArrayList.add(address);
                        onBackPressed();
                    }
                    else{
                        progress.setVisibility(View.GONE);
                        onBackPressed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.setVisibility(View.GONE);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestQueue.stop();
                progress.setVisibility(View.GONE);
            }
        });
        requestQueue.add(stringRequest);


    }


}
