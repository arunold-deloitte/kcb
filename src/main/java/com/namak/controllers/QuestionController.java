package com.namak.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.namak.config.DataInitializerConfig;
import com.namak.models.Question;
import com.namak.services.QuestionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class QuestionController {

    @Value("${namak.docs.path}")
    String docsPath;

    private final QuestionService questionService;

    public QuestionController(DataInitializerConfig dataInitializerConfig, QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/init-questions-gen")
    public String initializeQuestionsGen() {
        return questionService.loadQuestionsFromDocs(docsPath);
    }

    @GetMapping("/lobs")
    public Mono<List<String>> getLobs() {
        return questionService.getLobs();
    }

    @GetMapping("/sops")
    public Mono<List<String>> getSops(@RequestParam String lob) {
        return questionService.getSopByLob(lob);
    }

    // @GetMapping("/questions")
    // public Flux<Question> getQuestionsByLobAndSopAndCount(@RequestParam String lob, @RequestParam String sop,
    //         @RequestParam(defaultValue = "10", required = false) int count) {
    //     return questionService.getQuestionsByLobAndSopAndCount(lob, sop, count);
    // }

    @GetMapping("/questions")
    public Flux<Question> getQuestionsByLob(@RequestParam String lob) {
        return questionService.getQuestionsByLob(lob);
    }

    @DeleteMapping("/clear-questions")
    public String clearQuestions() {
        questionService.clearAllQuestions();
        return "All questions cleared";
    }
}