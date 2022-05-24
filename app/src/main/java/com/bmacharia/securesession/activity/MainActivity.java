package com.bmacharia.securesession.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bmacharia.securesession.R;
import com.bmacharia.securesession.model.User;
import com.bmacharia.securesession.util.SharedPrefManager;
import com.vstechlab.easyfonts.EasyFonts;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if the ser is not already logged in we will get login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.ic_launcher_foreground);
        getSupportActionBar().setTitle("Home");

        User user = SharedPrefManager.getInstance(this).getUser();

        TextView txtTitleUsername = (TextView) findViewById(R.id.txtTitleUsername);
        txtTitleUsername.setTypeface(EasyFonts.robotoBold(this));
        TextView txtUsername = (TextView) findViewById(R.id.txtUsername);
        txtUsername.setTypeface(EasyFonts.caviarDreams(this));
        txtUsername.setText(user.getUsername());
        TextView txtTitleEmail = (TextView) findViewById(R.id.txtTitleEmail);
        txtTitleEmail.setTypeface(EasyFonts.robotoBold(this));
        TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtEmail.setTypeface(EasyFonts.caviarDreams(this));
        txtEmail.setText(user.getEmail());
        TextView txtTitleAccessToken = (TextView) findViewById(R.id.txtTitleAccessToken);
        txtTitleAccessToken.setTypeface(EasyFonts.robotoBold(this));
        TextView txtAccessToken = (TextView) findViewById(R.id.txtAccessToken);
        txtAccessToken.setTypeface(EasyFonts.caviarDreams(this));
        txtAccessToken.setText(user.getAccessToken());
        TextView txtTitleRefreshToken = (TextView) findViewById(R.id.txtTitleRefreshToken);
        txtTitleRefreshToken.setTypeface(EasyFonts.robotoBold(this));
        TextView txtRefreshToken = (TextView) findViewById(R.id.txtRefreshToken);
        txtRefreshToken.setTypeface(EasyFonts.caviarDreams(this));
        txtRefreshToken.setText(user.getRefreshToken());

        Button btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnLogOut.setTypeface(EasyFonts.robotoBold(this));
        btnLogOut.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogOut) {
            SharedPrefManager.getInstance(this).logout();
            finish();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }
}