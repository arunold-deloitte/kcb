package com.namak.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.namak.models.Question;
import com.namak.services.GenAIService;
import com.namak.services.QuestionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @GetMapping("/lobs")
    public Mono<List<String>> getLobs() {
        return questionService.getLobs();
    }

    @GetMapping("/sops")
    public Mono<List<String>> getSops(@RequestParam String lob) {
        return questionService.getSopByLob(lob);
    }

    @GetMapping("/questions")
    public Flux<Question> getQuestionsByLobAndSopAndCount(@RequestParam String lob, @RequestParam String sop,
            @RequestParam(defaultValue = "10", required = false) int count) {
        return questionService.getQuestionsByLobAndSopAndCount(lob, sop, count);
    }

    @GetMapping("/quiz")
    public Flux<String> generateQuiz(@RequestParam String topic, @RequestParam int numQuestions) {
        return genAIService.generateQuiz(topic, numQuestions).doOnNext(System.out::println);
    }

    @GetMapping("/quiz-from-doc")
    public Flux<Question> generateQuizFromDoc(@RequestParam String docName) {
        return genAIService.generateAndSaveQuestionsFromDocument(docName);
    }
}