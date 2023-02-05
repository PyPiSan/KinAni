package com.pypisan.kinani.model;

public class ScheduleModel {

    private String title, image, episode, schedule;

    public ScheduleModel(String title, String image, String episode, String schedule) {
        this.title = title;
        this.image = image;
        this.episode = episode;
        this.schedule = schedule;
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
}
