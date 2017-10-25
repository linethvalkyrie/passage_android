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

    //For localhost (authetication)
    public static final String USERPWD = "admin:1234";

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