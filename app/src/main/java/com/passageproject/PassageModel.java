package com.passageproject;

/**
 * Created by Bray Santos on 23/10/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PassageModel {

    @SerializedName("passage_id")
    @Expose
    private String passageId;
    @SerializedName("passage_category")
    @Expose
    private String passageCategory;
    @SerializedName("passage_title")
    @Expose
    private String passageTitle;
    @SerializedName("passage_message")
    @Expose
    private String passageMessage;
    @SerializedName("passage_author")
    @Expose
    private String passageAuthor;

    public String getPassageId() {
        return passageId;
    }

    public void setPassageId(String passageId) {
        this.passageId = passageId;
    }

    public String getPassageCategory() {
        return passageCategory;
    }

    public void setPassageCategory(String passageCategory) {
        this.passageCategory = passageCategory;
    }

    public String getPassageTitle() {
        return passageTitle;
    }

    public void setPassageTitle(String passageTitle) {
        this.passageTitle = passageTitle;
    }

    public String getPassageMessage() {
        return passageMessage;
    }

    public void setPassageMessage(String passageMessage) {
        this.passageMessage = passageMessage;
    }

    public String getPassageAuthor() {
        return passageAuthor;
    }

    public void setPassageAuthor(String passageAuthor) {
        this.passageAuthor = passageAuthor;
    }
}
