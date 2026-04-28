package com.example.moviechatbot.service;

import org.springframework.stereotype.Service;

@Service
public class AiAnalysisService {

    public String normalizeQuery(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().toLowerCase();
    }

    public boolean isFollowUp(String input) {
        return input.contains("yes") || input.contains("another") || input.contains("more");
    }

    public boolean wantsFavorites(String input) {
        return input.contains("favorite") || input.contains("add") || input.contains("save");
    }

    public String explainRecommendation() {
        return "I looked for your favorite genre, year, or actor and chose a movie that fits your request.";
    }
}
