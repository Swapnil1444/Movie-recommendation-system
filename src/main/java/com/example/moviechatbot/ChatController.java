package com.example.moviechatbot;

import com.example.moviechatbot.model.Recommendation;
import com.example.moviechatbot.service.RecommendationService;
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
import java.util.List;

@Controller
@SessionAttributes({"messages", "lastGenre", "favorites", "lastRec"})
public class ChatController {

    private final CustomUserDetailsService userDetailsService;
    private final RecommendationService recommendationService;

    public ChatController(CustomUserDetailsService userDetailsService, RecommendationService recommendationService) {
        this.userDetailsService = userDetailsService;
        this.recommendationService = recommendationService;
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
        Recommendation recommendation = recommendationService.recommend(input, lastGenre);
        messages.add("Bot: " + recommendation.getMessage());

        if (!recommendation.getMessage().startsWith("Sorry")) {
            lastRec = recommendation.getMessage();
        }
        if (!recommendation.getGenre().isBlank()) {
            lastGenre = recommendation.getGenre();
            model.addAttribute("lastGenre", lastGenre);
        }
        messages.add("Bot: Would you like another recommendation? Say 'yes' or specify a new genre/year/actor.");

        model.addAttribute("messages", messages);
        model.addAttribute("favorites", favorites);
        model.addAttribute("lastRec", lastRec);
        return "chat";
    }
}
