package com.pypisan.kinani.api;

public class SignUpRequest {
    final String uid;
    final String user_name;
    final String password;
    final String age;
    final String gender;

    public SignUpRequest(String uid, String user_name, String password, String age, String gender) {
        this.uid = uid;
        this.user_name = user_name;
        this.password = password;
        this.age = age;
        this.gender = gender;
    }
}
