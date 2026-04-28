package com.example.moviechatbot.model;

public class Movie {
    private final String title;
    private final String genre;
    private final String year;
    private final String actor;

    public Movie(String title, String genre, String year, String actor) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.actor = actor;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getYear() {
        return year;
    }

    public String getActor() {
        return actor;
    }
}
