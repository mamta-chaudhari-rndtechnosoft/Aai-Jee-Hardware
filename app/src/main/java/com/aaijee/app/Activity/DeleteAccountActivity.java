package com.aaijee.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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

public class DeleteAccountActivity extends AppCompatActivity {

    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        btnDelete = findViewById(R.id.btnDeleteAccount);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });


    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        deleteAccount(true);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAccount(boolean isConfirmed) {
        // Retrieve the user_id from SharedPref
        String userId = SharedPref.getUserId(this);

        if (userId != null && !userId.isEmpty()) {
            // Construct the URL
            String url = Constants.delete_account + isConfirmed + "&user_id=" + userId;

            // Initialize the RequestQueue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            // Create a StringRequest
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                JSONObject userProfile = jsonResponse.getJSONArray("USER_PROFILE").getJSONObject(0);
                                String error = userProfile.getString("error");
                                String message = userProfile.getString("message");

                                if (error.equals("false")) {
                                    // Clear all shared preferences
                                    SharedPref.clearAllPreferences(DeleteAccountActivity.this);

                                    // Redirect to SignUpActivity and clear the task
                                    Intent intent = new Intent(DeleteAccountActivity.this, SignUpActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Handle the error
                                    Log.e("TAG", "Error: " + message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle the error
                    Log.e("TAG", "Error: " + error.getMessage());
                }
            });

            // Add the request to the RequestQueue
            requestQueue.add(stringRequest);
        } else {
            Log.e("TAG", "User ID not found in SharedPreferences");
        }
    }
}