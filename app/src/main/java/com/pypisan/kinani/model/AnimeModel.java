package com.pypisan.kinani.model;

public class AnimeModel {

    private String image, animeDetailLink, title, released, showType, releaseStatus;

    public String getReleaseStatus() {
        return releaseStatus;
    }

    public AnimeModel(String image, String animeDetailLink, String title, String released, String showType, String releaseStatus) {
        this.image = image;
        this.animeDetailLink = animeDetailLink;
        this.title = title;
        this.released = released;
        this.showType = showType;
        this.releaseStatus = releaseStatus;
    }

    public AnimeModel(){

    }

    public String getImage() {
        return image;
    }

    public String getAnimeDetailLink() {
        return animeDetailLink;
    }

    public String getTitle() {
        return title;
    }

    public String getReleased() {
        return released;
    }

    public String getShowType() {
        return showType;
    }
}
