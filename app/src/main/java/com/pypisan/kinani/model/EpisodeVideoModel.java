package com.pypisan.kinani.model;

import com.google.gson.annotations.SerializedName;

public class EpisodeVideoModel {

    @SerializedName("value")
    private final EpisodeVideoModel.datum value = null;

    @SerializedName("link")
    private final EpisodeVideoModel.dl link = null;
    @SerializedName("success")
    private Boolean success;

    public EpisodeVideoModel.datum getValue() {
        return value;
    }

    public EpisodeVideoModel.dl getLink() {
        return link;
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

    public class dl {
        @SerializedName("360")
        public String dlLink1;
        @SerializedName("480")
        public String dlLink2;
        @SerializedName("720")
        public String dlLink3;
        @SerializedName("1080")
        public String dlLink4;

        public String dlLink1() {
            return dlLink1;
        }

        public String dlLink2() {
            return dlLink2;
        }

        public String dlLink3() {
            return dlLink3;
        }

        public String dlLink4() {
            return dlLink4;
        }

    }

}
