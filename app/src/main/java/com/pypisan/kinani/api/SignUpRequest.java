package com.pypisan.kinani.api;

public class SignUpRequest {
    private  final String uid;
    private final String user_name;
    private final String password;
    private final String age;
    private final String gender;

    public SignUpRequest(String uid, String userName, String password, String age, String gender) {
        this.uid = uid;
        this.user_name = userName;
        this.password = password;
        this.age = age;
        this.gender = gender;
    }
}
