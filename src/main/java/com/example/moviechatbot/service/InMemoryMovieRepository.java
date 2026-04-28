package com.example.moviechatbot.service;

import com.example.moviechatbot.model.Movie;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class InMemoryMovieRepository implements MovieRepository {

    private final List<Movie> movies = Arrays.asList(
        new Movie("Mad Max: Fury Road", "action", "2015", "Tom Hardy"),
        new Movie("John Wick", "action", "2014", "Keanu Reeves"),
        new Movie("Die Hard", "action", "1988", "Bruce Willis"),
        new Movie("The Dark Knight", "action", "2008", "Christian Bale"),
        new Movie("Avengers: Endgame", "action", "2019", "Robert Downey Jr."),
        new Movie("The Grand Budapest Hotel", "comedy", "2014", "Ralph Fiennes"),
        new Movie("Superbad", "comedy", "2007", "Jonah Hill"),
        new Movie("Groundhog Day", "comedy", "1993", "Bill Murray"),
        new Movie("In Bruges", "comedy", "2008", "Colin Farrell"),
        new Movie("Shaun of the Dead", "comedy", "2004", "Simon Pegg"),
        new Movie("The Shawshank Redemption", "drama", "1994", "Tim Robbins"),
        new Movie("Forrest Gump", "drama", "1994", "Tom Hanks"),
        new Movie("Pulp Fiction", "drama", "1994", "John Travolta"),
        new Movie("Fight Club", "drama", "1999", "Brad Pitt"),
        new Movie("Inception", "drama", "2010", "Leonardo DiCaprio"),
        new Movie("The Conjuring", "horror", "2013", "Vera Farmiga"),
        new Movie("Hereditary", "horror", "2018", "Toni Collette"),
        new Movie("Get Out", "horror", "2017", "Daniel Kaluuya"),
        new Movie("It", "horror", "2017", "Bill Skarsgård"),
        new Movie("The Shining", "horror", "1980", "Jack Nicholson"),
        new Movie("Blade Runner 2049", "sci-fi", "2017", "Ryan Gosling"),
        new Movie("Interstellar", "sci-fi", "2014", "Matthew McConaughey"),
        new Movie("Arrival", "sci-fi", "2016", "Amy Adams"),
        new Movie("The Matrix", "sci-fi", "1999", "Keanu Reeves"),
        new Movie("Dune", "sci-fi", "2021", "Timothée Chalamet")
    );

    @Override
    public List<Movie> findAll() {
        return movies;
    }
}
