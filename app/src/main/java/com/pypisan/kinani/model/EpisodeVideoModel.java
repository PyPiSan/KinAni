package com.pypisan.kinani.model;

import com.google.gson.annotations.SerializedName;

public class EpisodeVideoModel {

    @SerializedName("value")
    private String value;
    @SerializedName("success")
    private Boolean success;

    public String getValue() {
        return value;
    }

    public Boolean getSuccess() {
        return success;
    }

}
