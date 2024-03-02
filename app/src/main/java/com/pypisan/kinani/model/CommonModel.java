package com.pypisan.kinani.model;

import com.google.gson.annotations.SerializedName;

public class CommonModel {

    @SerializedName("success")
    private Boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("password")
    private String password;

    public Boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getPassword() {
        return password;
    }
}
