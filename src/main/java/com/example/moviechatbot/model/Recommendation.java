package com.example.moviechatbot.model;

public class Recommendation {
    private final String message;
    private final String genre;

    public Recommendation(String message, String genre) {
        this.message = message;
        this.genre = genre;
    }

    public String getMessage() {
        return message;
    }

    public String getGenre() {
        return genre;
    }
}
