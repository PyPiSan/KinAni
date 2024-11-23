package com.pypisan.kinani.model;


public class SavedVideosModel {

    private String title, episode,showType;
    public SavedVideosModel(String title, String episode, String showType){
        this.title = title;
        this.episode = episode;
        this.showType = showType;
    }

    public  SavedVideosModel(){

    }

    public String getTitle() {
        return title;
    }

    public String getEpisode() {
        return episode;
    }

    public String getShowType() {
        return showType;
    }
}
