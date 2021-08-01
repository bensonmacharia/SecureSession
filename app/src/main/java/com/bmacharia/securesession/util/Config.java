package com.bmacharia.securesession.util;


public class Config {
    public static final String KEY_FNAME = "fullname";
    public static final String KEY_UNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASS = "password";

    //private static final String base_url = setBaseUrl();
    private static final String base_url = "http://127.0.0.1:5000/api/v1/";

    // Server user login url
    public static String URL_LOGIN = base_url+"user/login";

    // Server user register url
    public static String URL_REGISTER = base_url+"/user/register";

    // Get user profile url
    public static String URL_PROFILE = base_url+"user";
}