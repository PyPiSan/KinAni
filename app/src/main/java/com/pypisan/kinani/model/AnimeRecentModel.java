package com.pypisan.kinani.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AnimeRecentModel {

    @SerializedName("success")
    private Boolean success;
    @SerializedName("data")
    private final List<datum> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public List<datum> getData() {
        return data;
    }

    public class datum {

        @SerializedName("image_link")
        private String imageLink;
        @SerializedName("jtitle")
        private String jtitle;
        @SerializedName("title")
        private String title;

        public String getImageLink() {
            return imageLink;
        }

        public String getJtitle() {
            return jtitle;
        }

        public String getTitle() {
            return title;
        }

    }


}
