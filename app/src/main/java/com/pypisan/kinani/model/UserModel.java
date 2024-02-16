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

    @SerializedName("is_logged")
    private Boolean isLogged;

    @SerializedName("icon")
    private Integer icon;

    @SerializedName("user_name")
    private String userData;

    @SerializedName("notification")
    private String notification;

    public String getNotification() {
        return notification;
    }

    public String getUserData() {
        return userData;
    }

    public Integer getIcon() {
        return icon;
    }

    public Boolean getLogged() {
        return isLogged;
    }

    public String getApikey() {
        return apikey;
    }

    public Boolean getUserStatus() {return status;}

    public Boolean getAds(){return ads;}

    public String getMessage() {
        return message;
    }
}
