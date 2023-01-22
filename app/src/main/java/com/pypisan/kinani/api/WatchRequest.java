package com.pypisan.kinani.api;

public class WatchRequest {
    final String jtitle;
    final String episode_num;
    final String server_name;

    public WatchRequest(String jtitle, String episode_num, String server_name) {
        this.jtitle = jtitle;
        this.episode_num = episode_num;
        this.server_name = server_name;
    }
}
