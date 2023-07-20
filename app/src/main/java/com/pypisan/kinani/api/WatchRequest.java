package com.pypisan.kinani.api;

public class WatchRequest {
    final String title;
    final String episode_num;
    final String server_name;

    public WatchRequest(String title, String episode_num, String server_name) {
        this.title = title;
        this.episode_num = episode_num;
        this.server_name = server_name;
    }
}
