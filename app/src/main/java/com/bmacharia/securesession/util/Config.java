package com.bmacharia.securesession.util;


public class Config {
    public static final String KEY_FNAME = "name";
    public static final String KEY_UNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASS = "password";
    public static final String KEY_PASSC = "password_confirmation";

    //private static final String base_url = setBaseUrl();
    private static final String base_url = "https://bluezoneapi.egovernance.io/api/";

    // Server user login url
    public static String URL_LOGIN = base_url+"login";

    // Server user register url
    public static String URL_REGISTER = base_url+"register";
}