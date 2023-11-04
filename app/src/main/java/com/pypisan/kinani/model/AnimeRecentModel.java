package com.pypisan.kinani.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AnimeRecentModel {

    @SerializedName("success")
    private Boolean success;
    @SerializedName("results")
    private final List<datum> results = null;

    @SerializedName("total")
    private int resultSize;

    public Boolean getSuccess() {
        return success;
    }

    public List<datum> getData() {
        return results;
    }

    public int getResultSize(){return resultSize;}

    public class datum {

        @SerializedName("image")
        private String imageLink;
        @SerializedName("link")
        private String animeDetailLink;
        @SerializedName("title")
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
