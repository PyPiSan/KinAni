package com.pypisan.kinani.model;

import com.google.gson.annotations.SerializedName;

public class TriviaModel {
    @SerializedName("data")
    private final TriviaModel.datum data = null;
    @SerializedName("success")
    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public TriviaModel.datum getData() {
        return data;
    }

    public class datum {

        @SerializedName("image")
        private String imageUrl;

        @SerializedName("link")
        private String animeDetailLink;
        @SerializedName("title")
        private String title;
        @SerializedName("summary")
        private String summary;
        @SerializedName("episodes")
        private String episode_num;
        @SerializedName("status")
        private String status;
        @SerializedName("released")
        private String released;
        @SerializedName("question")
        private String question;
        @SerializedName("genres")
        private String[] genres;

        public String getImageLink() {
            return imageUrl;
        }

        public String getAnimeDetailLink() {
            return animeDetailLink;
        }

        public String getTitle() {
            return title;
        }

        public String getSummary() {
            return summary;
        }

        public String getEpisode_num() {
            return episode_num;
        }

        public String getStatus() { return status; }

        public String getReleased() { return released; }

        public String getQuestion() { return question; }

        public String[] getGenres(){ return genres;}
    }
}
