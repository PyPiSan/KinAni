package com.pypisan.kinani.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecentlyAiredModel {

    @SerializedName("results")
    private final List<RecentlyAiredModel.datum> results = null;
    @SerializedName("success")
    private Boolean success;

    public List<datum> getData() {
        return results;
    }

    public Boolean getSuccess() {
        return success;
    }

    public class datum {

        @SerializedName("image")
        private String image;
        @SerializedName("title")
        private String title;
        @SerializedName("episodes")
        private String episode;
        @SerializedName("schedule")
        private String schedule;
        @SerializedName("link")
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
