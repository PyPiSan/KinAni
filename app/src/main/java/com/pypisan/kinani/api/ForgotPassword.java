package com.pypisan.kinani.api;

public class ForgotPassword {

    final String user_name;
    final String uid;

    public ForgotPassword(String user_name, String uid) {
        this.user_name = user_name;
        this.uid = uid;
    }
}
