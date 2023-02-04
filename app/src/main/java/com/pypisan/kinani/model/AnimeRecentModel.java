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

        @SerializedName("image_url")
        private String imageLink;
        @SerializedName("anime_detail_link")
        private String animeDetailLink;
        @SerializedName("anime_title")
        private String title;
        @SerializedName("released")
        private String released;

        public String getImageLink() {
            return imageLink;
        }

        public String getAnimeDetailLink() {
            return animeDetailLink;
        }

        public String getTitle() {
            return title;
        }

        public String getReleased(){ return released;}

    }


}
