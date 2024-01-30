package com.pypisan.kinani.model;

public class ScheduleModel {

    private String title, image, episode, schedule,showType;

    public ScheduleModel(String title, String image, String episode, String schedule,String showType) {
        this.title = title;
        this.image = image;
        this.episode = episode;
        this.schedule = schedule;
        this.showType = showType;
    }

    public ScheduleModel(){

    }
    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getEpisode() {
        return episode;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getShowType() {
        return showType;
    }
}
