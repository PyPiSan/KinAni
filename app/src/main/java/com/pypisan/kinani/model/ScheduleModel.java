package com.pypisan.kinani.model;

public class ScheduleModel {

    private String jname, title, image, episode, schedule;

    public ScheduleModel(String jname, String title, String image, String episode, String schedule) {
        this.jname = jname;
        this.title = title;
        this.image = image;
        this.episode = episode;
        this.schedule = schedule;
    }

    public ScheduleModel(){

    }
    public String getJname() {
        return jname;
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
