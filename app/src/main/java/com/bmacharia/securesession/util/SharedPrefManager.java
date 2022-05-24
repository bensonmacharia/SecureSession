package com.bmacharia.securesession.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.bmacharia.securesession.activity.LoginActivity;
import com.bmacharia.securesession.model.User;

import java.io.IOException;
import java.security.GeneralSecurityException;

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

    // Encryption method
    public SharedPreferences getEncryptedSharedPreferences() {
        MasterKey masterKey = null;
        try {
            masterKey = new MasterKey.Builder(mCtx, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    mCtx,
                    USER_SHARED_PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sharedPreferences;
    }


    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        //SharedPreferences sharedPreferences = mCtx.getSharedPreferences(USER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = getEncryptedSharedPreferences().edit();
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
        //SharedPreferences sharedPreferences = mCtx.getSharedPreferences(USER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return getEncryptedSharedPreferences().getString(KEY_USERNAME, null) != null;
    }

    //this method will give the logged in user
    public User getUser() {
        //SharedPreferences sharedPreferences = mCtx.getSharedPreferences(USER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                getEncryptedSharedPreferences().getString(KEY_ID, null),
                getEncryptedSharedPreferences().getString(KEY_USERNAME, null),
                getEncryptedSharedPreferences().getString(KEY_EMAIL, null),
                getEncryptedSharedPreferences().getString(KEY_ROLE, null),
                getEncryptedSharedPreferences().getString(KEY_ATOKEN, null),
                getEncryptedSharedPreferences().getString(KEY_RTOKEN, null)
        );
    }

    //this method will logout the user
    public void logout() {
        //SharedPreferences sharedPreferences = mCtx.getSharedPreferences(USER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = getEncryptedSharedPreferences().edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }
}
