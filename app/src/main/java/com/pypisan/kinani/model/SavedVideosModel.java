package com.pypisan.kinani.model;


public class SavedVideosModel {

    private String title, episode,showType, filePath;
    public SavedVideosModel(String title, String episode, String showType, String filePath){
        this.title = title;
        this.episode = episode;
        this.showType = showType;
        this.filePath = filePath;
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

    public String getFilePath() {
        return filePath;
    }
}
