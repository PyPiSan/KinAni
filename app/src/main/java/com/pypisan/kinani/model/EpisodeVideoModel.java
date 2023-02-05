package com.pypisan.kinani.model;

import com.google.gson.annotations.SerializedName;

public class EpisodeVideoModel {

    @SerializedName("value")
    private final EpisodeVideoModel.datum value = null;
    @SerializedName("success")
    private Boolean success;

    public EpisodeVideoModel.datum getValue() {
        return value;
    }

    public Boolean getSuccess() {
        return success;
    }

    public class datum {
        @SerializedName("360")
        public String quality1;
        @SerializedName("480")
        public String quality2;
        @SerializedName("720")
        public String quality3;
        @SerializedName("1080")
        public String quality4;

        public String getQuality1() {
            return quality1;
        }

        public String getQuality2() {
            return quality2;
        }

        public String getQuality3() {
            return quality3;
        }

        public String getQuality4() {
            return quality4;
        }

    }

}
