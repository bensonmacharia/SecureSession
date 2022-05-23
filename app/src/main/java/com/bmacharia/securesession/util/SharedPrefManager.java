package com.bmacharia.securesession.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.bmacharia.securesession.activity.LoginActivity;
import com.bmacharia.securesession.model.User;

public class SharedPrefManager {
    //the constants
    private static final String USER_SHARED_PREF_NAME = "usersharedpref";
    private static final String KEY_ID = "keyid";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_FNAME = "keyfirstname";
    private static final String KEY_LNAME = "keylastname";
    private static final String KEY_ROLE = "keyrole";
    private static final String KEY_ATOKEN = "keyaccesstoken";
    private static final String KEY_RTOKEN = "keyrefreshtoken";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(USER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ID, user.getUserId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_ATOKEN, user.getAccessToken());
        editor.putString(KEY_RTOKEN, user.getRefreshToken());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(USER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    //this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(USER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_ID, null),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_ROLE, null),
                sharedPreferences.getString(KEY_ATOKEN, null),
                sharedPreferences.getString(KEY_RTOKEN, null)
        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(USER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }
}
