package com.aaijee.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaijee.app.Adapter.AddressListAdapter;
import com.aaijee.app.Adapter.SearchLocationAdapter;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchDeliveryLocationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    ProgressBar progressBar;
    ArrayList<Address> addressArrayList;
    private RecyclerView recyclerView;
    private TextView saved_addresses,maptext;
    LinearLayout selectViaMap;
    EditText searchTextView;
    private SearchLocationAdapter searchLocationAdapter;
    PlacesClient placesClient;
    private AddressListAdapter addressAdapter;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_search_delivery_location);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchTextView = (EditText) findViewById(R.id.searchTextView);
        saved_addresses = (TextView) findViewById(R.id.saved_addresses);
        selectViaMap = (LinearLayout) findViewById(R.id.selectViaMap);
        placesClient = Places.createClient(this);
        progressBar = findViewById(R.id.progresbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        progressBar.setVisibility(View.GONE);

        getAddressList();
        selectViaMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectViaMap.setClickable(false);
                Intent intent = new Intent(SearchDeliveryLocationActivity.this, MapsActivity.class);
                startActivityForResult(intent, Constants.MAPACTIVITY);
                finish();

            }


        });
        img = (ImageView) findViewById(R.id.img);
        maptext = (TextView) findViewById(R.id.maptext);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img.setClickable(false);
                Intent intent = new Intent(SearchDeliveryLocationActivity.this, MapsActivity.class);
                startActivityForResult(intent,Constants.MAPACTIVITY);
                finish();

            }
        });
        maptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maptext.setClickable(false);
                Intent intent = new Intent(SearchDeliveryLocationActivity.this, MapsActivity.class);
                startActivityForResult(intent,Constants.MAPACTIVITY);
                finish();

            }
        });
        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                setLocationList(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectViaMap.setClickable(true);
        img.setClickable(true);
        maptext.setClickable(true);
    }

    public void getAddressList() {
        progressBar.setVisibility(View.VISIBLE);
        String params= SharedPref.getUserId(this);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.view_address +params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("SHIPPING_ADDRESS_LIST");
                    if(addressArrayList==null)
                        addressArrayList = new ArrayList<>();
                    else
                        addressArrayList.clear();
                    for(int i=0;i<jsonArray.length();i++) {

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        Address address= new Address();

                        String error=jsonObject1.getString("error");
                        if (error.equals("false")) {
                            String id = jsonObject1.getString("id");
                            String user_id = jsonObject1.getString("user_id");
                            String flat_no = jsonObject1.getString("flat_no");
                            String building_name = jsonObject1.getString("building_name");
                            String landmark = jsonObject1.getString("landmark");
                            String city = jsonObject1.getString("city");
                            String pincode = jsonObject1.getString("pincode");
                            String name = jsonObject1.getString("name");
                            String email = jsonObject1.getString("email");
                            String mobile = jsonObject1.getString("mobile");
                            String add_type = jsonObject1.getString("address_type");

                            String latitude = jsonObject1.getString("latitude");
                            String longitude = jsonObject1.getString("longitude");

                            //address.setId(id);
                            address.setUser_id(user_id);
                            address.setFlat_no(flat_no);
                            address.setBuilding_name(building_name);
                            address.setLandmark(landmark);
                            address.setCity(city);
                            address.setName(name);
                            address.setLatitude(latitude);
                            address.setLongitude(longitude);
                            address.setPincode(pincode);
                            address.setEmail(email);
                            address.setMobile(mobile);
                            address.setAddress_type(add_type);

                            addressArrayList.add(address);
                        }
                    }
                    if(addressArrayList.size()!=0)
                        setAdapter();
                    else{
                        recyclerView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestQueue.stop();
                progressBar.setVisibility(View.GONE);
            }
        });
        requestQueue.add(stringRequest);

    }

    private void setAdapter() {

        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

                Constants.CITY = addressArrayList.get(position).getAddress_type();
                finish();
            }
        };
        searchLocationAdapter = new SearchLocationAdapter(SearchDeliveryLocationActivity.this,addressArrayList,listener);
        recyclerView.setAdapter(searchLocationAdapter);
        progressBar.setVisibility(View.GONE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Constants.MAPACTIVITY)
        {

            if(addressArrayList!=null) {
                addressArrayList.clear();
                searchLocationAdapter.arrayList.clear();
                searchLocationAdapter.notifyDataSetChanged();
                getAddressList();
            }
        }
    }

    void fetchLatLong(String placeId)
    {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                try {
                    Intent intent = new Intent(SearchDeliveryLocationActivity.this, MapsActivity.class);
                    intent.putExtra(Constants.ADDRESS, place.getAddress());
                    intent.putExtra(Constants.LATITUDE, place.getLatLng().latitude);
                    intent.putExtra(Constants.LONGITUDE, place.getLatLng().longitude);
                    startActivityForResult(intent,Constants.MAPACTIVITY);
                    finish();
                }catch (Exception e)
                {}
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                }
            }
        });

    }

    void setLocationList(String text)
    {
        if(addressArrayList==null)
            addressArrayList = new ArrayList<>();
        else
            addressArrayList.clear();

        if(searchLocationAdapter!=null)
        {
            searchLocationAdapter.arrayList.clear();
            searchLocationAdapter.notifyDataSetChanged();
        }

        progressBar.setVisibility(View.VISIBLE);
        saved_addresses.setVisibility(View.GONE);
        Places.initialize(getApplicationContext(), Constants.APIKEY);

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        // Create a RectangularBounds object.
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596));


        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(bounds)
                .setCountry("in")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(text)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
                RecyclerViewClickListener listener = new RecyclerViewClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        String placeId = addressArrayList.get(position).getPlaceId();
                        fetchLatLong(placeId);
                    }
                };
                int i=0;
                for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                    Address address = new Address();
                    address.setCity(prediction.getPrimaryText(null).toString());
                    address.setPlaceId(prediction.getPlaceId());
                    address.setAddress_type("map");
                    address.setName(prediction.getFullText(null).toString());
                    addressArrayList.add(address);
                }

                searchLocationAdapter = new SearchLocationAdapter(SearchDeliveryLocationActivity.this,addressArrayList,listener);
                recyclerView.setAdapter(searchLocationAdapter);
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                progressBar.setVisibility(View.GONE);
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;

                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchDeliveryLocationActivity.this);
                    builder.setMessage("Oops! \n"+exception.getMessage())
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        });
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

}
