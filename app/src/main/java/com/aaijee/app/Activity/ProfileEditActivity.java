package com.aaijee.app.Activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;

import com.aaijee.app.R;
import com.aaijee.app.Util.AppHelper;
import com.aaijee.app.Util.Constants;
import com.aaijee.app.Util.PostImageChooseDialog;
import com.aaijee.app.Util.SharedPref;
import com.aaijee.app.Util.VolleyMultipartRequest;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
 

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import de.hdodenhof.circleimageview.BuildConfig;
import com.aaijee.app.BuildConfig;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditActivity extends AppCompatActivity implements PostImageChooseDialog.AlertPositiveListener
{

    private static String APP_TEMP_FOLDER="";
    Toolbar toolbar;
    ProgressBar progressBar;
    String name,email_id,pgender,pimage,pmobile,pwallet,pdob,pdoa,paddress,plocation;
    CircleImageView profile_image,image_edit;
    EditText tvusername,tvemail,tvgender,tvdob,tvdoa;
    Button btnSubmit;
    private String selectedPostImg = "";
    private Uri selectedImage;
    private Uri outputFileUri;
    public static final int SELECT_POST_IMG = 3;
    public static final int CREATE_POST_IMG = 5;
    private int mYear, mMonth, mDay;
    Spinner spingender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_profile_edit);

        toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setTitle(Html.fromHtml("<b>"+getResources().getString(R.string.edit_profile)+"</b>"));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progress_profile);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
       image_edit = (CircleImageView) findViewById(R.id.image);
        tvusername = (EditText) findViewById(R.id.tvusername);
        tvemail = (EditText) findViewById(R.id.tvemail);
        tvgender = (EditText) findViewById(R.id.tvgender);
        tvdob = (EditText) findViewById(R.id.tvdob);
        tvdoa = (EditText) findViewById(R.id.tvdoa);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        spingender = (Spinner) findViewById(R.id.spingender);

        List<String> categories = new ArrayList<String>();
        categories.add("Please select Gender");
        categories.add("Male");
        categories.add("Female");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spingender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvgender.setText(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // attaching data adapter to spinner
        spingender.setAdapter(dataAdapter);

        tvgender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spingender.performClick();
            }
        });

        APP_TEMP_FOLDER = getResources().getString(R.string.app_name);

        if (selectedPostImg != null && selectedPostImg.length() > 0) {

            profile_image.setImageURI(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", new File(selectedPostImg)));
        }

        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateProfile();
            }
        });

        image_edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //choiceImage();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (selectedPostImg.length() == 0)
                {

                  //  choiceImage();

                }
            }
        });

        tvdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(tvdob);
            }
        });

        tvdoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(tvdoa);
            }
        });

        getProfile();

    }

    private void datePicker(final EditText date) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    public void choiceImage() {

        FragmentManager fm =  this.getSupportFragmentManager();

        PostImageChooseDialog alert = new PostImageChooseDialog();
        alert.show(fm, "alert_dialog_image_choose");
    }

    private void updateProfile()
    {
        final RequestQueue requestQueue = Volley.newRequestQueue(ProfileEditActivity.this);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constants.edit_profile, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                // parse success output
                finish();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String> params = new HashMap<>();
                params.put("name", tvusername.getText().toString());
                params.put("email", tvemail.getText().toString());
                params.put("gender", tvgender.getText().toString());
                params.put("dob", tvdob.getText().toString());
                params.put("doa", tvdoa.getText().toString());
                params.put("user_id", SharedPref.getUserId(ProfileEditActivity.this));
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("image", new DataPart("profile.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), profile_image.getDrawable()), "image/jpeg"));
                return params;
            }
        };
        requestQueue.add(multipartRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getProfile() {
        progressBar.setVisibility(View.VISIBLE);
        final String userid= SharedPref.getUserId(ProfileEditActivity.this);
        final RequestQueue requestQueue = Volley.newRequestQueue(ProfileEditActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.view_profile+userid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String res = new String(response);
                try {
                    JSONObject jsonObject = new JSONObject(res);

                    JSONArray jsonArray = jsonObject.getJSONArray("USER_PROFILE");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        name = object.getString("name");
                        email_id = object.getString("email");
                        pmobile = object.getString("mobile");
                        pgender = object.getString("gender");
                        pimage = object.getString("image");
                        pwallet = object.getString("wallet");
                        pdob = object.getString("dob");
                        pdoa = object.getString("doa");
                        paddress = object.getString("address");
                        plocation = object.getString("location");
                    }

                    if (!name.equals(""))
                        tvusername.setText(name);
                    if (!email_id.equals(""))
                        tvemail.setText(email_id);
                    if (pgender.equals("Male"))
                        spingender.setSelection(1);
                    else if (pgender.equals("Female"))
                        spingender.setSelection(2);
                    else
                        spingender.setSelection(0);
                    if (!pimage.equals(""))
                   //     Glide.with(ProfileEditActivity.this).load(pimage).thumbnail(Glide.with(ProfileEditActivity.this).load(R.drawable.loading)).into(profile_image);
                    if (!pdob.equals(""))
                        tvdob.setText(pdob);
                    if (!pdoa.equals(""))
                        tvdoa.setText(pdoa);

                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(VideoDetailActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                requestQueue.stop();
                progressBar.setVisibility(View.GONE);
            }
        });
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onImageFromGallery() {
        imageFromGallery();
    }

    @Override
    public void onImageFromCamera() {
        imageFromCamera();
    }

    public void imageFromGallery()
    {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getText(R.string.label_select_img)), SELECT_POST_IMG);
    }

    public void imageFromCamera() {

        try {

            File root = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.app_name));

            if (!root.exists()) {

                root.mkdirs();
            }

            File sdImageMainDirectory = new File(root, "photo.jpg");

            outputFileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", sdImageMainDirectory);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(cameraIntent, CREATE_POST_IMG);


        } catch (Exception e) {

            Toast.makeText(this, "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_POST_IMG && resultCode == RESULT_OK && null != data)
        {

            selectedImage = data.getData();

            selectedPostImg = getImageUrlWithAuthority(this, selectedImage, "photo.jpg");

            try {

                selectedPostImg = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "photo.jpg";

                profile_image.setImageURI(Uri.fromFile(new File(selectedPostImg)));

            } catch (Exception e) {


            }

        } else if (requestCode == CREATE_POST_IMG && resultCode == this.RESULT_OK)
        {

            try {

                selectedPostImg = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "photo.jpg";

                profile_image.setImageURI(Uri.fromFile(new File(selectedPostImg)));

            } catch (Exception ex) {


            }

        }
    }

    public static String getImageUrlWithAuthority(Context context, Uri uri, String fileName) {

        InputStream is = null;

        if (uri.getAuthority() != null) {

            try {

                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);

                return writeToTempImageAndGetPathUri(context, bmp, fileName).toString();

            } catch (FileNotFoundException e) {

                e.printStackTrace();

            } finally {

                try {

                    if (is != null) {

                        is.close();
                    }

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static String writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage, String fileName)
    {

        String file_path = inContext.getExternalFilesDir( Environment.DIRECTORY_PICTURES).toString()+ File.separator + APP_TEMP_FOLDER;
//                Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER;
        File dir = new File(file_path);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, fileName);

        try
        {

            FileOutputStream fos = new FileOutputStream(file);

            inImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();

        }
        catch (FileNotFoundException e)
        {

            Toast.makeText(inContext, "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();

        } catch (IOException e)
        {

            e.printStackTrace();
        }

        return inContext.getExternalFilesDir( Environment.DIRECTORY_PICTURES).toString() + File.separator + APP_TEMP_FOLDER + File.separator + "photo.jpg";
    }
}
