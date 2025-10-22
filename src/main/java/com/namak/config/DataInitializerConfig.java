package com.namak.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.namak.services.QuestionService;

@Configuration
public class DataInitializerConfig {

	@Value("${namak.docs.path}")
	String docsPath;

	@Bean
	@ConditionalOnProperty(name = "questions.load-init", havingValue = "true")
	public CommandLineRunner initQuestionsGen(QuestionService questionService) {
		return args -> {
			questionService.loadQuestionsFromDocs(docsPath);
		};
	}
}
