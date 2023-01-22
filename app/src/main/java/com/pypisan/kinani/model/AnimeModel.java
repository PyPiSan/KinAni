package com.pypisan.kinani.model;

public class AnimeModel {

    private String image, jtitle, title;

    public AnimeModel(String image, String jtitle, String title) {
        this.image = image;
        this.jtitle = jtitle;
        this.title = title;
    }

    public AnimeModel(){

    }

    public String getImage() {
        return image;
    }

    public String getJtitle() {
        return jtitle;
    }

    public String getTitle() {
        return title;
    }

}
