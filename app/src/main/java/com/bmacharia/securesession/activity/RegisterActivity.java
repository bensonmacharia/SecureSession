package com.bmacharia.securesession.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bmacharia.securesession.R;
import com.bmacharia.securesession.model.User;
import com.bmacharia.securesession.util.Config;
import com.bmacharia.securesession.util.ConnectionDetector;
import com.bmacharia.securesession.util.SharedPrefManager;
import com.bmacharia.securesession.util.VolleySingleton;
import com.vstechlab.easyfonts.EasyFonts;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private EditText editTextUserName, editTextFirstName, editTextLastName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegistration;
    private TextView txtBtnLogin;

    private ProgressDialog pDialog;

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_register);

        editTextUserName = findViewById(R.id.editTextUserName);
        editTextUserName.setTypeface(EasyFonts.caviarDreams(this));
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextFirstName.setTypeface(EasyFonts.caviarDreams(this));
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextLastName.setTypeface(EasyFonts.caviarDreams(this));
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmail.setTypeface(EasyFonts.caviarDreams(this));
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPassword.setTypeface(EasyFonts.caviarDreams(this));
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextConfirmPassword.setTypeface(EasyFonts.caviarDreams(this));
        txtBtnLogin = findViewById(R.id.txtBtnLogin);
        txtBtnLogin.setTypeface(EasyFonts.robotoBold(this));

        buttonRegistration = findViewById(R.id.buttonRegistration);
        buttonRegistration.setOnClickListener(this);
        txtBtnLogin.setOnClickListener(this);

    }

    private void validateSignUpForm(){
        final String username = editTextUserName.getText().toString();
        final String first_name = editTextFirstName.getText().toString();
        final String last_name = editTextLastName.getText().toString();
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        final String confrim_password = editTextConfirmPassword.getText().toString();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //validating inputs
        if (TextUtils.isEmpty(username)) {
            editTextUserName.setError("Please enter your Username");
            editTextUserName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(first_name)) {
            editTextFirstName.setError("Please enter your First Name");
            editTextFirstName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(last_name)) {
            editTextLastName.setError("Please enter your Last Name");
            editTextLastName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email) || !isValidEmail(email)) {
            editTextEmail.setError("Please enter a valid Email Address");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Enter a password");
            editTextPassword.requestFocus();
            return;
        }
        if (!validatePassword(password, confrim_password)) {
            editTextConfirmPassword.setError("Both passwords must be the same");
            editTextConfirmPassword.requestFocus();
            return;
        }
        if (!validateLengthPassword(password)) {
            editTextPassword.setError("Password too short");
            editTextPassword.requestFocus();
            return;
        }
        submitSignUpForm(username, first_name, last_name, email, password);
    }

    private void submitSignUpForm(String username, String fname, String lname, String email, String password){
        // creating connection detector class instance
        cd = new ConnectionDetector(RegisterActivity.this);
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();
        pDialog.setMessage("Registering ...");
        showDialog();
        Log.i(TAG, "RegisterUrl " + Config.URL_REGISTER);
        if (isInternetPresent) {
            // Post params to be sent to the server
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Config.KEY_UNAME, username);
            params.put(Config.KEY_FNAME, fname);
            params.put(Config.KEY_LNAME, lname);
            params.put(Config.KEY_EMAIL, email);
            params.put(Config.KEY_PASS, password);
            Log.i(TAG, "RegisterParams " + params);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_REGISTER, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "RegisterResponse: " + response);
                            hideDialog();
                            try {
                                VolleyLog.v("Response:%n %s", response.toString(4));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "RegisterError " + e.getMessage());
                                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideDialog();
                    VolleyLog.e("Error: ", error.getMessage());
                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // add the request object to the queue to be executed
            Log.e(TAG, "RegisterRequest " + jsonObjectRequest);
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

        } else {
            hideDialog();
            Toast.makeText(RegisterActivity.this, "No internet connection! Try saving again.", Toast.LENGTH_LONG).show();
        }
    }

    private void submitSignUpForm1(String username, String fname, String lname, String email, String password){
        // creating connection detector class instance
        cd = new ConnectionDetector(RegisterActivity.this);

        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        pDialog.setMessage("Registering ...");
        showDialog();
        //String registerUrl = URLs.URL_REGISTER+"?full_name="+full_name+"&car_reg="+car_reg+"&phone_no="+phone_no+"&password="+password;
        Log.i(TAG, "RegisterUrl " + Config.URL_REGISTER);
        if (isInternetPresent) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_REGISTER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "RegisterResponse: " + response);
                            hideDialog();
                            try {
                                //converting response to json object
                                JSONObject obj = new JSONObject(response);

                                Log.i(TAG, "RegisterObj" + obj);

                                if (obj.get("code").equals("200")){
                                    String message = obj.getString("message");
                                    Log.i(TAG, "RegisterMessage" + message);
                                    if (message.equals("User registered successfully!")){
                                        Toast.makeText(getApplicationContext(), "Successfully registered. Login", Toast.LENGTH_SHORT).show();
                                        //starting the main activity
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error Registering. Try again", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error Registering. Try again", Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "RegisterError " + e.getMessage());
                                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            //Log.e(TAG, "RegisterVolleyError " + error);
                            //Toast.makeText(getApplicationContext(), "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            hideDialog();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(getApplicationContext(), "Communication Error!", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(getApplicationContext(), "Authentication Error!", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {
                                Toast.makeText(getApplicationContext(), "Server Side Error!", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(getApplicationContext(), "Parse Error!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error Registering!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(Config.KEY_UNAME, username);
                    params.put(Config.KEY_FNAME, fname);
                    params.put(Config.KEY_LNAME, lname);
                    params.put(Config.KEY_EMAIL, email);
                    params.put(Config.KEY_PASS, password);
                    Log.i(TAG, "RegisterParams " + params);
                    return params;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        } else {
            hideDialog();
            Toast.makeText(RegisterActivity.this, "No internet connection! Try saving again.", Toast.LENGTH_LONG).show();
        }
    }

    private static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean validatePassword(String pass, String confirm_pass) {
        return pass.equals(confirm_pass);
    }

    private boolean validateLengthPassword(String pass) {
        return pass.length() >= 6;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonRegistration:
                validateSignUpForm();
                break;
            case R.id.txtBtnLogin:
                finish();
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                break;
        }
    }
}
