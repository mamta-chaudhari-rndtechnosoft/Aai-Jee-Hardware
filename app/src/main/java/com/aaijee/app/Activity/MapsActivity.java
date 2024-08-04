package com.aaijee.app.Activity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Toolbar toolbar;
    private Marker finalPerth;
    private double latitude;
    private double longitude;
    private  String address;
    private Button btnNext;
    private TextView txthome, txtoffice, txtothers,location;
    private EditText edtFlat,edtBuilding,edtlandmark,edtcity,edtpincode;
    private String name,email,mbl,address_type;
    String params;
    private String url,area_name,area_id;
    private Geocoder geocoder;
    private ProgressBar progressBar;
    private PlacesClient placesClient;
    LocationManager locationManager;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;
    public final static int ALL_PERMISSIONS_RESULT = 101;
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    int selected_area;
    Spinner spinner;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_maps);

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Create a RectangularBounds object.
        placesClient = Places.createClient(this);
        // Use the builder to create a FindAutocompletePredictionsRequest.
        geocoder = new Geocoder(this, Locale.getDefault());
        initializeView();
        address = getIntent().getStringExtra(Constants.ADDRESS);
        latitude = getIntent().getDoubleExtra(Constants.LATITUDE,0.0);
        longitude = getIntent().getDoubleExtra(Constants.LONGITUDE,0.0);
        if(latitude==0.0 && longitude ==0.0)
            findCurrentLocation();
        else {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        toolbar.setTitle("Select Location");
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    void configureLocationComponent()
    {
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);

        if (!isGPS && !isNetwork) {
            showSettingsAlert();
            getLastLocation();

        } else {
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    canGetLocation = false;
                }
            }
            // get location
            checkLocation();
        }
    }


    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {e.printStackTrace();}
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    void findCurrentLocation()
    {

        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            placesClient.findCurrentPlace(request).addOnSuccessListener((new OnSuccessListener<FindCurrentPlaceResponse>() {
                @Override
                public void onSuccess(FindCurrentPlaceResponse findCurrentPlaceResponse) {

                    for (PlaceLikelihood placeLikelihood : findCurrentPlaceResponse.getPlaceLikelihoods()) {
                        //  textView.append(String.format("Place '%s' has likelihood: %f\n",
                        Constants.CITY=placeLikelihood.getPlace().getAddress();
                        latitude = (placeLikelihood.getPlace().getLatLng().latitude);
                        longitude = (placeLikelihood.getPlace().getLatLng().longitude);
                        address = placeLikelihood.getPlace().getAddress();
                        location.setText(placeLikelihood.getPlace().getAddress());
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(MapsActivity.this);
                        break;
                    }
                    // updateProfile(name,email);
                }
            })).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                    }

                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            configureLocationComponent();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        boolean canUseExternalStorage = false;

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findCurrentLocation();
                    canUseExternalStorage = true;
                }
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        progressBar.setVisibility(View.GONE);
        // Add a marker in Sydney and move the camera
        if(latitude!=0.0 && longitude !=0.0){

            LatLng vapi = new LatLng(latitude, longitude);
            finalPerth = mMap.addMarker(new MarkerOptions().position(vapi).title(""));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(vapi));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(vapi, 12.0f));
            location.setText(address);

        }else {
            LatLng vapi = new LatLng(20.3893, 72.9106);
            finalPerth = mMap.addMarker(new MarkerOptions().position(vapi).title("Marker in Vapi"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(vapi));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(vapi, 12.0f));
        }

        Marker perth = null;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(finalPerth == null) {
                    finalPerth = mMap.addMarker(new MarkerOptions().position(latLng));
                } else {
                    finalPerth.setPosition(latLng);
                }
                List<Address> addresses = new ArrayList<>();
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Address address = addresses.get(0);

                if (address != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++){
                        sb.append(address.getAddressLine(i) + "\n");
                        location.setText(sb);
                    }
                    if(address.getAddressLine(0)!=null || address.getAddressLine(0).equals(""))
                        location.setText(address.getAddressLine(0));
                    //Toast.makeText(MapsActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
            }
        });


    }

    public void initializeView()
    {
        progressBar = (ProgressBar) findViewById(R.id.progresbar);

        edtFlat = findViewById(R.id.edtFlat);
        edtBuilding = findViewById(R.id.edtBuilding);
        edtlandmark = findViewById(R.id.edtlandmark);
        edtcity = findViewById(R.id.edtcity);
        edtpincode = findViewById(R.id.edtpincode);
        btnNext=findViewById(R.id.next);
        txthome=findViewById(R.id.txthome);
        txtoffice=findViewById(R.id.txtoffice);
        txtothers=findViewById(R.id.txtothers);
        location=findViewById(R.id.location);


        txthome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txthome.setBackground(getResources().getDrawable(R.drawable.rounded_btn));
                txtoffice.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
                txtothers.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
                address_type="home";
            }
        });

        txtoffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtoffice.setBackground(getResources().getDrawable(R.drawable.rounded_btn));
                txthome.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
                txtothers.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
                address_type="office";
            }
        });

        txtothers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtothers.setBackground(getResources().getDrawable(R.drawable.rounded_btn));
                txthome.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
                txtoffice.setBackground(getResources().getDrawable(R.drawable.rounded_gray_border_btn));
                address_type="others";
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String locationText = location.getText().toString();
                String flat = edtFlat.getText().toString();
                String building = edtBuilding.getText().toString();
                String landmark = edtlandmark.getText().toString();
                String city = edtcity.getText().toString();
                String pincode = edtpincode.getText().toString();
                name = SharedPref.getUserName(MapsActivity.this);
                email = SharedPref.getPreference(Constants.EMAIL,"",MapsActivity.this);
                mbl = SharedPref.getMobileNumber(MapsActivity.this);
                if(locationText.equals("") || flat.equals("") || building.equals("")|| landmark.equals("") || city.equals("") || pincode.equals(""))
                {
                    Toast.makeText(MapsActivity.this, "Oops! Some required field are empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(address_type==null || address_type.equals(""))
                {
                    Toast.makeText(MapsActivity.this, "Please select address type(Home/Office/Others)", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (area_id==null && area_id.equalsIgnoreCase("")){
                    Toast.makeText(MapsActivity.this, "Please Select Area", Toast.LENGTH_SHORT).show();
                    return;
                }

                newAddress(name, mbl, email, flat, building, landmark, city, pincode);
            }
        });
    }

    public void newAddress(String name, String mbl, String email, String flat, String building, String landmark, String city, String pincode) {
        //progressBar.setVisibility(View.VISIBLE);
        params = "&user_id=" + SharedPref.getUserId(this) + "&flat_no=" + flat + "&building_name=" + building + "&landmark=" + landmark + "&area=" + area_id + "&city=" + city + "&pincode=" + pincode + "&name=" + name + "&email=" + email + "&mobile=" + mbl + "&address_type=" + address_type+"&latitude="+latitude+"&longitude="+longitude+"&location="+location.getText().toString();
        url=Constants.new_address;
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url +params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("DELIVERY_ADDRESS");
                    String error = jsonObject1.getString("error");
                    if (error.equals("false")) {
                        String city = jsonObject1.getString("city");

                        Constants.CITY = city;
                        finish();


                    }else{
                    }
                    progressBar.setVisibility(View.GONE);
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

}