package com.pypisan.kinani.model;

import com.google.gson.annotations.SerializedName;

public class SignUpModel {

    @SerializedName("success")
    private Boolean status;

    @SerializedName("message")
    private String message;

    public Boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
