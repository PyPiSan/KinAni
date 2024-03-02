package com.pypisan.kinani.api;

public class UserInit {
    final String uid;
    final String origin;
    final String os;
    final String version;

    public UserInit(String uid, String origin, String os, String version) {
        this.uid = uid;
        this.origin = origin;
        this.os = os;
        this.version = version;
    }
}
