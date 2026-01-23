package com.moneymanger.moneytracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    /**
     * Converts user spoken text into a clean category name.
     * No restriction on categories.
     */
    public String extractCategory(String userText) {

        // Safety fallback
        if (userText == null || userText.trim().isEmpty()) {
            return "Unknown";
        }

        String prompt = """
        You are an assistant for a money manager application.

        Task:
        - Convert the user's spoken words into a clean category name.
        - Capitalize the first letter of each word.
        - Keep it short (maximum 3 words).
        - Do NOT restrict to any predefined list.
        - Do NOT add explanations, quotes, or extra text.

        Examples:
        "salary" -> Salary
        "monthly income" -> Monthly Income
        "bike servicing" -> Bike Servicing
        "mom medicine" -> Mom Medicine
        "online course fee" -> Online Course Fee

        User said: "%s"

        Return ONLY the category name.
        """.formatted(userText);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiUrl + "?key=" + apiKey,
                    entity,
                    Map.class
            );

            String geminiText = extractText(response.getBody());

            // Final safety: if Gemini returns empty
            if (geminiText == null || geminiText.isBlank()) {
                return normalize(userText);
            }

            return geminiText;

        } catch (Exception e) {
            // If Gemini fails, just use user text
            return normalize(userText);
        }
    }

    /**
     * Extracts text safely from Gemini response
     */
    private String extractText(Map body) {
        try {
            List candidates = (List) body.get("candidates");
            Map content = (Map) ((Map) candidates.get(0)).get("content");
            List parts = (List) content.get("parts");

            String text = parts.get(0).toString();
            return text.replace("{text=", "").replace("}", "").trim();

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fallback formatting if Gemini is unavailable
     */
    private String normalize(String text) {
        String[] words = text.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (String w : words) {
            if (w.isEmpty()) continue;
            sb.append(Character.toUpperCase(w.charAt(0)))
                    .append(w.substring(1))
                    .append(" ");
        }
        return sb.toString().trim();
    }
}
