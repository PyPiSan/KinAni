package com.pypisan.kinani.model;

import com.google.gson.annotations.SerializedName;

public class UserModel {

    @SerializedName("x-api-key")
    private String apikey;
    @SerializedName("success")
    private Boolean status;

    @SerializedName("ads")
    private Boolean ads;

    @SerializedName("message")
    private String message;

    public String getApikey() {
        return apikey;
    }

    public Boolean getUserStatus() {return status;}

    public Boolean getAds(){return ads;}

    public String getMessage() {
        return message;
    }
}
