package com.pypisan.kinani.api;

public class UserUpdate {

    final String uid;
    final Integer icon;
    final Boolean is_logged;
    final Boolean is_delete;

    final Boolean terms;

    public UserUpdate(String uid, Integer icon, Boolean is_logged, Boolean isDelete, Boolean terms) {
        this.uid = uid;
        this.icon = icon;
        this.is_logged = is_logged;
        this.is_delete = isDelete;
        this.terms = terms;
    }
}
