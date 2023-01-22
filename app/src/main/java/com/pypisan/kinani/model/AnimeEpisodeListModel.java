package com.pypisan.kinani.model;

import com.google.gson.annotations.SerializedName;

public class AnimeEpisodeListModel {
    @SerializedName("data")
    private final AnimeEpisodeListModel.datum data = null;
    @SerializedName("success")
    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public AnimeEpisodeListModel.datum getData() {
        return data;
    }

    public class datum {

        @SerializedName("image_link")
        private String image_link;

        @SerializedName("jtitle")
        private String jtitle;
        @SerializedName("title")
        private String title;
        @SerializedName("summary")
        private String summary;
        @SerializedName("episode_num")
        private int episode_num;

        public String getImage_link() {
            return image_link;
        }

        public String getJtitle() {
            return jtitle;
        }

        public String getTitle() {
            return title;
        }

        public String getSummary() {
            return summary;
        }

        public int getEpisode_num() {
            return episode_num;
        }
    }
}
