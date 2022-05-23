package com.bmacharia.securesession.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private LinearLayout layoutSignU;
    private EditText editTextPassword, editTextUserName;
    private Button buttonLogin;
    private TextView txtForgotPass;
    private TextView txtBtnRegister;
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
        setContentView(R.layout.activity_login);

        layoutSignU = (LinearLayout) findViewById(R.id.sign_up_layout);
        layoutSignU.setOnClickListener(this);
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextUserName.setTypeface(EasyFonts.caviarDreams(this));
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPassword.setTypeface(EasyFonts.caviarDreams(this));
        txtForgotPass = (TextView) findViewById(R.id.txtForgotPass);
        txtForgotPass.setTypeface(EasyFonts.robotoBold(this));

        txtBtnRegister = (TextView) findViewById(R.id.txtBtnRegister);
        txtBtnRegister.setTypeface(EasyFonts.robotoBold(this));

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setTypeface(EasyFonts.robotoBold(this));

        txtBtnRegister.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
    }

    private void validateSignInForm() {
        final String password = editTextPassword.getText().toString();
        final String username = editTextUserName.getText().toString();
        //Toast.makeText(getApplicationContext(), car_reg, Toast.LENGTH_LONG).show();

        //validating inputs
        if (TextUtils.isEmpty(username)) {
            editTextUserName.setError("Please enter your Username");
            editTextUserName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Enter a password");
            editTextPassword.requestFocus();
            return;
        }
        signInSubmit(username, password);
    }

    private void signInSubmit(String username, String password){
        // creating connection detector class instance
        cd = new ConnectionDetector(LoginActivity.this);
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();
        pDialog.setMessage("Login in ...");
        showDialog();
        Log.i(TAG, "LoginUrl " + Config.URL_LOGIN);
        if (isInternetPresent) {
            // Post params to be sent to the server
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Config.KEY_UNAME, username);
            params.put(Config.KEY_PASS, password);
            Log.i(TAG, "LoginParams " + params);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_LOGIN, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "LoginResponse: " + response);
                            hideDialog();
                            try {
                                //converting response to json object
                                if (response.getString("type").equals("Bearer")) {
                                    Toast.makeText(getApplicationContext(), "Successfully logged in. Welcome", Toast.LENGTH_SHORT).show();
                                    String res_type = response.getString("type");
                                    String res_id = response.getString("id");
                                    String res_uname = response.getString("username");
                                    String res_email = response.getString("email");
                                    String res_role = response.getString("roles");
                                    String ac_token = response.getString("accessToken");
                                    String rf_token = response.getString("refreshToken");

                                    User user = new User(res_id, res_uname, res_email, res_role, ac_token, rf_token);
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                    Toast.makeText(getApplicationContext(), "Logged In. Welcome!", Toast.LENGTH_SHORT).show();

                                    //starting the main activity
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error Login in. Try again", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "LoginError " + e.getMessage());
                                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
                        Toast.makeText(getApplicationContext(), "Error Login!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // add the request object to the queue to be executed
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

        } else {
            hideDialog();
            Toast.makeText(LoginActivity.this, "No internet connection! Try saving again.", Toast.LENGTH_LONG).show();
        }
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
            case R.id.txtBtnRegister:
                finish();
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                break;
            case R.id.buttonLogin:
                validateSignInForm();
                break;
        }
    }
}
