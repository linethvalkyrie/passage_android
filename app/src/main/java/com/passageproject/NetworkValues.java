package com.passageproject;

import android.util.Base64;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Bray Santos on 8/4/2017.
 */

public class NetworkValues {

    public static final String API = "passage_api";
    public static final String SERVER_URL = "https://dev-portal.tmn.ph/tmn_assist_server/api/";
    //For Dev
//    public static final String USERPWD = "atin_to:6dffd8dbd8560c405662fec842671456b48176ae";
    //For localhost
    public static final String USERPWD = "admin:1234";

    //qa/uat
//    public static String CERTIFICATE_PATH = "cert/2a2a445aacf20fc6.crt";
    //prod
//    public static String CERTIFICATE_PATH = "cert/f9fdad3b279707c2.crt";

    public static final String BASIC_AUTH = String.format("Basic %s", Base64.encodeToString(
            String.format("%s",USERPWD).getBytes(), Base64.NO_WRAP));

    public static HashMap<String, String> getHeaders() {
        HashMap<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/json; charset=utf-8");
//        headers.put("Content-Type","application/x-www-form-urlencoded");
        headers.put("Authorization",BASIC_AUTH);
        Log.i("authorization:", BASIC_AUTH);
        return headers;
    }
}