package com.namak.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.namak.models.Question;
import com.namak.services.GenAIService;
import com.namak.services.QuestionService;

import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private final GenAIService genAIService;
    private final QuestionService questionService;

    public ChatController(GenAIService genAIService, QuestionService questionService) {
        this.genAIService = genAIService;
        this.questionService = questionService;
    }

    @GetMapping("/chat")
    public Flux<String> chat(@RequestParam String prompt) {
        return genAIService.chat(prompt);
    }

    @GetMapping("/quiz")
    public Flux<String> generateQuiz(@RequestParam String topic, @RequestParam int numQuestions) {
        return genAIService.generateQuiz(topic, numQuestions).doOnNext(System.out::println);
    }

    @GetMapping("/quiz-from-doc")
    public Flux<Question> generateQuizFromDoc(@RequestParam String docName, @RequestParam String lob) {
        return questionService.saveQuestions(genAIService.generateQuestionsFromDocument(docName, lob));

    }
}