package com.passageproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HttpVolley.VolleyCallback, PassageRecyclerViewAdapter.PassageModelListener {

    private RecyclerView recyclerView;
    private PassageRecyclerViewAdapter myPassageRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rvAnnouncement);

        HttpVolley.ConnectionBuilder.newInstance()
                .isHttps(true)
                .setContext(this)
                .setVolleyCallback(this)
                .setServerUrl(NetworkValues.SERVER_URL)
                .setRequestMethod(Request.Method.GET)
                .setParams(new HashMap<String, String>())
                .setApi("passage_api")
                .setTask("getPassage")
                .build();
    }

    @Override
    public void onRequestSuccess(JSONObject response) throws JSONException {
        PassageData passageData = new Gson().fromJson(response.toString(), PassageData.class);
        List<PassageModel> passageModelArrayList = new ArrayList<>();

        if (passageData.getResponseCode().equalsIgnoreCase("0")) {
            passageModelArrayList = passageData.getData();

            myPassageRecyclerViewAdapter = new PassageRecyclerViewAdapter(passageModelArrayList, this);
            recyclerView.setAdapter(myPassageRecyclerViewAdapter);
        }
    }

    @Override
    public void onRequestFail(JSONObject response) {

    }

    @Override
    public HashMap<String, String> setHeader() {
        return NetworkValues.getHeaders();
    }

    @Override
    public void onPassageClick(PassageModel passageModel) {

    }
}
