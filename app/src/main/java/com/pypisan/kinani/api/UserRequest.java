package com.pypisan.kinani.api;

public class UserRequest {

    final String user_name;
    final String password;

    public UserRequest(String user_name, String password) {
        this.user_name = user_name;
        this.password = password;
    }
}
