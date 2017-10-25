package com.passageproject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import static com.android.volley.Request.Method.POST;

/**
 * Created by Bray Santos on 7/27/2016.
 * This class utilizes Volley library for networking by google.
 */
public class HttpVolley {

    private String certificatePath;
    private String serverUrl;
    private boolean isHttps = false;

    public interface VolleyCallback {
        /**
         * @param response
         * @throws JSONException
         */
        void onRequestSuccess(JSONObject response) throws JSONException;
        void onRequestFail(JSONObject response);
        /**
         * @return hashMap with header key and values specified in the implementing class
         */
        HashMap<String,String> setHeader();
    }

    /**
     * This private Constructor is called from ConnectionBuilder class's build() method.
     */
    private HttpVolley() {
        buildConnection();
    }

    enum CommonResponseKey {
        RESPONSE("response"),
        MESSAGE("message"),
        ERROR_MESSAGE("error_message"),
        ERROR_CODE("error_code"),
        RESPONSE_MESSAGE("response_message"),
        RESPONSE_CODE("response_code"),
        SUCCESS("success"),
        SUCCESS_MSG("success_message"),
        ERRORS("errors"),
        PAGE_NUMBER("page_no"),
        ITEM_CODE("item_code"),
        LIMIT("limit");

        private String key;

        CommonResponseKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    private final String CONNECTION_REFUSED = "Cannot connect to server.";
    private final String NO_INTERNET_CONNECTION = "An error occurred, This can be due to internet connection problem. Please try again.";
    private final String INTERNAL_ERROR = "Internal error.";
    private final String INVALID_URL = "Invalid URL.";
    private final String CONNECTION_LOST = "Connection lost.";


    public static final int ERR_CONNECTION_REFUSED = -1;
    public static final int ERR_NO_INTERNET_CONNETION = -2;
    public static final int ERR_INTERNAL_ERROR = -3;
    public static final int ERR_INVALID_URL = -4;
    public static final int ERR_CONNECTION_LOST = -5;

    private final String TAG = this.getClass().getSimpleName();
    private int timeoutSocket = 500000;
    private Context context;
    // This string error message template is for internal network error. Ex. No internet connection, Connection Refuse, Connection lost etc...
    private String errorResponse = "{\""+ CommonResponseKey.RESPONSE_CODE.getKey()+"\":\"["+ CommonResponseKey.ERROR_CODE.getKey()+"]\",\""
            + CommonResponseKey.ERROR_MESSAGE.getKey()+"\":\"["+ CommonResponseKey.ERROR_MESSAGE.getKey()+"]\"}";
//    private String errorResponse = "{\"success\":false,\"response\":{\"error_message\":\"["+ CommonResponseKey.ERROR_MESSAGE.getKey()+"]\"}}";
    private RequestQueue requestQueue;

    private VolleyCallback callback;
    private String api;
    private String task;
    private HashMap<String,String> params;
    private int method;

    /**
     * For POST Method. Params mandatory. if no params set it to null.
     *
     * @param c
     * @param callback
     * @param api
     * @param task
     * @param params
     */
    public void wPost(Context c, VolleyCallback callback, String api, String task, HashMap<String,String> params) {
        createRequest(c, callback, api, POST , task, params);
    }



    public static class ConnectionBuilder {

        private static Context context;
        private static VolleyCallback callback;
        private static String api;
        private static String task;
        private static HashMap<String,String> params;
        private static int method;
        private static String serverUrl;
        private static String certificatePath;
        private static boolean isHttps;

        public static ConnectionBuilder newInstance() {
            return new ConnectionBuilder();
        }

        public ConnectionBuilder setContext(Context context) {
            this.context = context;
            return this;
        }

        public  ConnectionBuilder setVolleyCallback(VolleyCallback callback) {
            this.callback = callback;
            return this;
        }

        public ConnectionBuilder setApi(String api) {
            this.api = api;
            return this;
        }

        public ConnectionBuilder setRequestMethod(int method) {
            this.method = method;
            return this;
        }

        public ConnectionBuilder setTask(String task) {
            this.task = task;
            return this;
        }

        public ConnectionBuilder setParams(HashMap<String,String> params) {
            this.params = params;
            return this;
        }

        public ConnectionBuilder setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }

        public ConnectionBuilder setCertificatePath(String certificatePath) {
            this.certificatePath = certificatePath;
            return this;
        }

        public ConnectionBuilder isHttps(boolean isHttps) {
            this.isHttps = isHttps;
            return this;
        }

        public void build() {
            new HttpVolley();
        }
    }

    private void buildConnection() {
        this.context = ConnectionBuilder.context;
        this.callback = ConnectionBuilder.callback;
        this.api = ConnectionBuilder.api;
        this.task = ConnectionBuilder.task;
        this.method = ConnectionBuilder.method;
        this.params = ConnectionBuilder.params;
        this.serverUrl = ConnectionBuilder.serverUrl;
        this.certificatePath = ConnectionBuilder.certificatePath;
        this.isHttps = ConnectionBuilder.isHttps;
        callMethod();
    }

    private void callMethod() {
        switch (method) {
            case POST:
                wPost(context,callback,api,task,params);
                break;
            default:
                if(params != null)
                    wGet(context,callback,api,task,params);
                else
                    wGet(context,callback,api,task);
        }
    }

    /**
     * For Get Method with params
     * @param c
     * @param callback
     * @param api
     * @param task
     * @param params
     */
    public void wGet(Context c, VolleyCallback callback, String api, String task, HashMap<String,String> params) {
        createRequest(c, callback, api, Request.Method.GET , task, params);
    }

    /**
     * For GET Method with no params
     * @param c
     * @param callback
     * @param api
     * @param task
     */
    public void wGet(Context c, VolleyCallback callback , String api, String task) {
        createRequest(c, callback, api, Request.Method.GET , task, null);
    }

    /**
     * This creates a request {POST,GET}
     * @param c - the context of caller object/class.
     * @param task - method inside controller. ex. (server)/(api)/{task}
     * @param params - these are the values to be passed on to server.
     * @param method - values for this are constants Request.Method interface.
     * @param api - values for this are our web api controllers
     */
    private void createRequest(Context c, VolleyCallback callback, String api, int method, String task, HashMap<String,String> params) {

        System.setProperty("http.keepAlive", "false");
        if(!isConnected(c)) {
            errorResponse = createInternalErrorMsg(
            NO_INTERNET_CONNECTION);
            callback.onRequestFail(createJSONOnError(errorResponse));
            return;
        }
        connectWithVolleyJSONRequest(c,callback,api,method,task,params);
    }

    private String createInternalErrorMsg(String errorMessage) {
        /*return errorResponse.replace("["+CommonResponseKey.ERROR_CODE.getKey()+"]", errorCode)
                                    .replace("["+CommonResponseKey.ERROR_MESSAGE.getKey()+"]",errorMessages);*/
        return errorResponse.replace("["+ CommonResponseKey.ERROR_MESSAGE.getKey()+"]",errorMessage);
    }

    private void connectWithVolleyJSONRequest(Context c, final VolleyCallback callback, String api, int method, String task, HashMap<String, String> params) {
        this.context = c;
        String url = serverUrl + api + "/" + task;
        Log.i(this.getClass().getSimpleName(),"API url = "+url);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(method,url,new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG,"response = "+response);
                        try {
                            callback.onRequestSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG,"response = "+error);
                        errorResponse = createInternalErrorMsg(CONNECTION_REFUSED);
                        callback.onRequestFail(createJSONOnError(errorResponse));
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("devtoken", "QBFJ!2170G^XXUCLXC7@QV*7");
                headers.put("userlang", "en");
                headers.put("cache-control", "no-cache");*/
                return callback.setHeader();
            }
        };

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(timeoutSocket,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if(isHttps) {
            requestQueue = Volley.newRequestQueue(context, hurlStack);
        } else {
            requestQueue = Volley.newRequestQueue(context);
        }
        jsonRequest.setTag(task);
        requestQueue.add(jsonRequest);
    }

    private JSONObject createJSONOnError(String errorResponse) {
        try {
            return new JSONObject(errorResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HurlStack hurlStack = new HurlStack() {
        @Override
        protected HttpURLConnection createConnection(URL url) throws IOException {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
            try {
                httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory(context));
                httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return httpsURLConnection;
        }
    };

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify(hostname, session);
            }
        };
    }

    /**
     * set ServerConstants.CERTIFICATE_PATH to get rid of the exceptions.
     * @return
     * @throws CertificateException
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public SSLSocketFactory getSSLSocketFactory(Context context) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = context.getResources().getAssets().open(certificatePath); // this cert file stored in \app\src\main\res\raw folder path

        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

    public static boolean isConnected(Context context){
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isConnected();
    }

    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }
}

