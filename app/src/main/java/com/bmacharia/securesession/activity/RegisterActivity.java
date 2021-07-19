package com.bmacharia.securesession.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bmacharia.securesession.R;
import com.vstechlab.easyfonts.EasyFonts;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private EditText editTextFullName, editTextUserName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegistration;
    private TextView txtBtnLogin;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextFullName.setTypeface(EasyFonts.caviarDreams(this));
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextUserName.setTypeface(EasyFonts.caviarDreams(this));
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
        final String full_name = editTextFullName.getText().toString();
        final String username = editTextUserName.getText().toString();
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        final String confrim_password = editTextConfirmPassword.getText().toString();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //validating inputs
        if (TextUtils.isEmpty(full_name)) {
            editTextFullName.setError("Please enter your Full Name");
            editTextFullName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            editTextUserName.setError("Please enter your Username");
            editTextUserName.requestFocus();
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
        submitSignUpForm(full_name, username, email, password);
    }

    private void submitSignUpForm(String name, String username, String email, String password){

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
