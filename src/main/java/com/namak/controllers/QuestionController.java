package com.namak.controllers;

import java.util.List;

import org.checkerframework.common.returnsreceiver.qual.This;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.namak.config.DataInitializerConfig;
import com.namak.models.Question;
import com.namak.services.GenAIService;
import com.namak.services.QuestionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class QuestionController {

    private final DataInitializerConfig dataInitializerConfig;
    private final QuestionService questionService;
    private final GenAIService genAIService;
    private final ResourcePatternResolver resourcePatternResolver;

    public QuestionController(DataInitializerConfig dataInitializerConfig, QuestionService questionService,
            GenAIService genAIService, ResourcePatternResolver resourcePatternResolver) {
        this.dataInitializerConfig = dataInitializerConfig;
        this.questionService = questionService;
        this.genAIService = genAIService;
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @PostMapping("/init-questions-gen")
    public String initializeQuestionsGen() {
        this.dataInitializerConfig.initQuestionsGen(genAIService, resourcePatternResolver);
        return "Questions generation initialized";
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

    @DeleteMapping("/clear-questions")
    public String clearQuestions() {
        questionService.clearAllQuestions();
        return "All questions cleared";
    }
}