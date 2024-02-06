package com.pypisan.kinani.api;

import android.text.BoringLayout;

public class UserUpdate {

    final String uid;
    final Integer icon;
    final Boolean is_logged;

    public UserUpdate(String uid, Integer icon, Boolean is_logged) {
        this.uid = uid;
        this.icon = icon;
        this.is_logged = is_logged;
    }
}
