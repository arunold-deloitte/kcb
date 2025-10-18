package com.namak.namak.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.namak.namak.services.ChatService;

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

    @GetMapping("/quiz")
    public Flux<String> generateQuiz(@RequestParam String topic, @RequestParam int numQuestions) {
        return chatService.generateQuiz(topic, numQuestions).doOnNext(System.out::println);
    }
}