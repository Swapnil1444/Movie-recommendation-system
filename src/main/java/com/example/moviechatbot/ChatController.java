package com.example.moviechatbot;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Controller
@SessionAttributes({"messages", "lastGenre", "favorites", "lastRec"})
public class ChatController {

    private final Random random = new Random();
    private final CustomUserDetailsService userDetailsService;

    private static class Movie {
        String title, genre, year, actor;
        Movie(String t, String g, String y, String a) { title = t; genre = g; year = y; actor = a; }
    }

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

    public ChatController(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, Model model) {
        if (userDetailsService.userExists(username)) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        userDetailsService.registerUser(username, password);
        return "redirect:/login?registered";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("username", auth.getName());
            if (!model.containsAttribute("favorites")) {
                model.addAttribute("favorites", new ArrayList<String>());
            }
        } else {
            return "redirect:/login";
        }
        return "profile";
    }

    @GetMapping("/")
    public String chat(Model model, SessionStatus status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            if (!model.containsAttribute("messages")) {
                List<String> messages = new ArrayList<>();
                messages.add("Bot: Hello " + auth.getName() + "! Tell me your favorite genre, year, actor, or say 'random' for a recommendation.");
                model.addAttribute("messages", messages);
            }
            if (!model.containsAttribute("lastGenre")) {
                model.addAttribute("lastGenre", "");
            }
            if (!model.containsAttribute("favorites")) {
                model.addAttribute("favorites", new ArrayList<String>());
            }
            if (!model.containsAttribute("lastRec")) {
                model.addAttribute("lastRec", "");
            }
        } else {
            return "redirect:/login";
        }
        return "chat";
    }

    @PostMapping("/recommend")
    public String recommend(@RequestParam String input, Model model) {
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) model.getAttribute("messages");
        if (messages == null) {
            messages = new ArrayList<>();
        }
        String lastGenre = (String) model.getAttribute("lastGenre");
        if (lastGenre == null) lastGenre = "";
        @SuppressWarnings("unchecked")
        List<String> favorites = (List<String>) model.getAttribute("favorites");
        if (favorites == null) favorites = new ArrayList<>();
        String lastRec = (String) model.getAttribute("lastRec");
        if (lastRec == null) lastRec = "";

        messages.add("You: " + input);
        String inputLower = input.toLowerCase();

        if (inputLower.contains("favorite") || inputLower.contains("add")) {
            if (!lastRec.isEmpty()) {
                favorites.add(lastRec);
                messages.add("Bot: Added to favorites!");
            } else {
                messages.add("Bot: No recent recommendation to add.");
            }
        } else if (inputLower.contains("yes") || inputLower.contains("another") || inputLower.contains("more")) {
            if (lastGenre.isEmpty()) {
                messages.add("Bot: What genre would you like?");
            } else {
                String recommendation = getRecommendation(lastGenre, "", "");
                messages.add("Bot: " + recommendation);
                lastRec = recommendation;
                messages.add("Bot: Would you like another recommendation? Say 'yes' or specify a new genre/year/actor.");
            }
        } else {
            // Parse input
            String genre = parseGenre(inputLower);
            String year = parseYear(inputLower);
            String actor = parseActor(inputLower);
            if (genre.isEmpty() && year.isEmpty() && actor.isEmpty() && !inputLower.contains("random")) {
                messages.add("Bot: Please specify a genre, year, actor, or say 'random'.");
            } else {
                String recommendation = getRecommendation(genre, year, actor);
                messages.add("Bot: " + recommendation);
                lastRec = recommendation;
                lastGenre = genre;
                messages.add("Bot: Would you like another recommendation? Say 'yes' or specify a new genre/year/actor.");
            }
            model.addAttribute("lastGenre", lastGenre);
        }
        model.addAttribute("messages", messages);
        model.addAttribute("favorites", favorites);
        model.addAttribute("lastRec", lastRec);
        return "chat";
    }

    private String getRecommendation(String genre, String year, String actor) {
        List<Movie> filtered = movies.stream()
            .filter(m -> genre.isEmpty() || m.genre.equalsIgnoreCase(genre))
            .filter(m -> year.isEmpty() || m.year.equals(year))
            .filter(m -> actor.isEmpty() || m.actor.toLowerCase().contains(actor.toLowerCase()))
            .collect(Collectors.toList());
        if (filtered.isEmpty()) {
            return "Sorry, no movies match your criteria. Try different genre/year/actor.";
        }
        Movie rec = filtered.get(random.nextInt(filtered.size()));
        return "I recommend: " + rec.title + " (" + rec.year + ") starring " + rec.actor + ". Genre: " + rec.genre + ".";
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
        // Simple: look for 4 digits
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\b\\d{4}\\b");
        java.util.regex.Matcher m = p.matcher(input);
        if (m.find()) return m.group();
        return "";
    }

    private String parseActor(String input) {
        // Simple: if contains known actor names
        String[] actors = {"tom hardy", "keanu reeves", "bruce willis", "christian bale", "robert downey jr", "ralph fiennes", "jonah hill", "bill murray", "colin farrell", "simon pegg", "tim robbins", "tom hanks", "john travolta", "brad pitt", "leonardo dicaprio", "vera farmiga", "toni collette", "daniel kaluuya", "bill skarsgård", "jack nicholson", "ryan gosling", "matthew mcconaughey", "amy adams", "timothée chalamet"};
        for (String a : actors) {
            if (input.contains(a)) return a;
        }
        return "";
    }
}