package com.example.moviechatbot.service;

import com.example.moviechatbot.model.Movie;
import com.example.moviechatbot.model.Recommendation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final MovieRepository movieRepository;
    private final AiAnalysisService aiAnalysisService;
    private final Random random = new Random();

    public RecommendationService(MovieRepository movieRepository, AiAnalysisService aiAnalysisService) {
        this.movieRepository = movieRepository;
        this.aiAnalysisService = aiAnalysisService;
    }

    public Recommendation recommend(String input, String lastGenre) {
        String normalized = aiAnalysisService.normalizeQuery(input);
        if (normalized.isBlank()) {
            return new Recommendation("Please enter a genre, movie, year, or actor to get a recommendation.", lastGenre);
        }

        if (aiAnalysisService.isFollowUp(normalized) && !lastGenre.isBlank()) {
            return createRecommendation(lastGenre, "", "", "Here is another recommendation based on your previous genre: ");
        }

        String genre = parseGenre(normalized);
        String year = parseYear(normalized);
        String actor = parseActor(normalized);

        if (genre.isBlank() && year.isBlank() && actor.isBlank() && !normalized.contains("random")) {
            return new Recommendation("Please specify a genre, year, actor, or say 'random'.", lastGenre);
        }

        if (normalized.contains("random")) {
            return createRecommendation("", "", "", "I found a random recommendation for you: ");
        }

        return createRecommendation(genre, year, actor, "I recommend: ");
    }

    private Recommendation createRecommendation(String genre, String year, String actor, String prefix) {
        List<Movie> movies = movieRepository.findAll().stream()
            .filter(m -> genre.isEmpty() || m.getGenre().equalsIgnoreCase(genre))
            .filter(m -> year.isEmpty() || m.getYear().equals(year))
            .filter(m -> actor.isEmpty() || m.getActor().toLowerCase().contains(actor.toLowerCase()))
            .collect(Collectors.toList());

        if (movies.isEmpty()) {
            return new Recommendation("Sorry, no movies match your criteria. Try a different genre, year, or actor.", genre);
        }

        Movie movie = movies.get(random.nextInt(movies.size()));
        String message = prefix + movie.getTitle() + " (" + movie.getYear() + ") starring " + movie.getActor() + ". Genre: " + movie.getGenre() + ".";
        return new Recommendation(message, genre.isEmpty() ? movie.getGenre() : genre);
    }

    private String parseGenre(String input) {
        if (input.contains("action")) return "action";
        if (input.contains("comedy") || input.contains("funny")) return "comedy";
        if (input.contains("drama")) return "drama";
        if (input.contains("horror") || input.contains("scary")) return "horror";
        if (input.contains("sci-fi") || input.contains("science fiction")) return "sci-fi";
        return "";
    }

    private String parseYear(String input) {
        Pattern pattern = Pattern.compile("\\b\\d{4}\\b");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    private String parseActor(String input) {
        String[] actors = {
            "tom hardy", "keanu reeves", "bruce willis", "christian bale", "robert downey jr",
            "ralph fiennes", "jonah hill", "bill murray", "colin farrell", "simon pegg",
            "tim robbins", "tom hanks", "john travolta", "brad pitt", "leonardo dicaprio",
            "vera farmiga", "toni collette", "daniel kaluuya", "bill skarsgård", "jack nicholson",
            "ryan gosling", "matthew mcconaughey", "amy adams", "timothée chalamet"
        };
        for (String actor : actors) {
            if (input.contains(actor)) {
                return actor;
            }
        }
        return "";
    }
}
