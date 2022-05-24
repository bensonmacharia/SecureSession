package com.bmacharia.securesession.util;


public class Config {
    public static final String KEY_UNAME = "username";
    public static final String KEY_FNAME = "firstname";
    public static final String KEY_LNAME = "lastname";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASS = "password";

    //private static final String base_url = setBaseUrl();
    private static final String base_url = "https://springauthservice.herokuapp.com/api/";

    // Server user login url
    public static String URL_LOGIN = base_url + "user/signin";

    // Server user register url
    public static String URL_REGISTER = base_url + "user/signup";

    // Get user profile url
    public static String URL_PROFILE = base_url + "res/user/profile";
}