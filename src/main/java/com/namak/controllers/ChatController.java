package com.namak.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.namak.models.Question;
import com.namak.services.ChatService;

import reactor.core.publisher.Flux;

@RestController
public class ChatController {
    
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat")
    public Flux<String> chat(@RequestParam String prompt) {
        return chatService.chat(prompt);
    }

    @GetMapping("/questions")
    public Flux<Question> getQuestions(@RequestParam(defaultValue = "10", required = false) int count) {
        return chatService.getQuestions(count);
    }

    @GetMapping("/quiz")
    public Flux<String> generateQuiz(@RequestParam String topic, @RequestParam int numQuestions) {
        return chatService.generateQuiz(topic, numQuestions).doOnNext(System.out::println);
    }

    @GetMapping("/quiz-from-doc")
    public Flux<Question> generateQuizFromDoc(@RequestParam String docName) {
        return chatService.generateAndSaveQuestionsFromDocument(docName);
    }
}