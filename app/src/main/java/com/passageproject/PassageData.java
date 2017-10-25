package com.passageproject;

/**
 * Created by Bray Santos on 23/10/2017.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PassageData {

    @SerializedName("Data")
    @Expose
    private List<PassageModel> data = null;
    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;
    @SerializedName("ResponseMsg")
    @Expose
    private String responseMsg;

    public List<PassageModel> getData() {
        return data;
    }

    public void setData(List<PassageModel> data) {
        this.data = data;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }
}
