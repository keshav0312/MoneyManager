package com.moneymanger.moneytracker.controller;
import com.moneymanger.moneytracker.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class VoiceController {

private  final GeminiService  geminiService;


    @PostMapping("/voice/category")
      public Map<String, String> detectCategory(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        String category = geminiService.extractCategory(text);
        return Map.of("category", category);
    }

}
