package com.pypisan.kinani.model;

public class ContinueWatchingModel {

    private String title, detail,image, episode, showType;
    private Integer time;

    public ContinueWatchingModel(){

    }

    public ContinueWatchingModel(String title, String detail, String image,
                                 String episode, String showType, Integer time) {
        this.title = title;
        this.detail = detail;
        this.image = image;
        this.episode = episode;
        this.showType = showType;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public String getImage() {
        return image;
    }

    public String getEpisode() {
        return episode;
    }

    public String getShowType() {
        return showType;
    }

    public Integer getTime() {
        return time;
    }
}
