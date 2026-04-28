package com.example.moviechatbot.service;

import com.example.moviechatbot.model.Movie;
import java.util.List;

public interface MovieRepository {
    List<Movie> findAll();
}
