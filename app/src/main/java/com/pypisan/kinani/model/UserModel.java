package com.pypisan.kinani.model;

import com.google.gson.annotations.SerializedName;

public class UserModel {

    @SerializedName("x-api-key")
    private String apikey;
    @SerializedName("success")
    private Boolean status;

    public String getApikey() {
        return apikey;
    }

    public Boolean getUserStatus() {
        return status;
    }
}
