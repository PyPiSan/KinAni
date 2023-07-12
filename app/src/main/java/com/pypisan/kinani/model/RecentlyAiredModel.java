package com.pypisan.kinani.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecentlyAiredModel {

    @SerializedName("data")
    private final List<RecentlyAiredModel.datum> data = null;
    @SerializedName("success")
    private Boolean success;

    public List<datum> getData() {
        return data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public class datum {

        @SerializedName("image_url")
        private String image;
        @SerializedName("anime_title")
        private String title;
        @SerializedName("episodes")
        private String episode;
        @SerializedName("schedule")
        private String schedule;
        @SerializedName("anime_detail_link")
        private String detailLink;

        public String getImage() {
            return image;
        }

        public String getTitle() {
            return title;
        }

        public String getEpisode() {
            return episode;
        }

        public String getSchedule() {
            return schedule;
        }
    }
}
