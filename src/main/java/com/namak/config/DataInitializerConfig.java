package com.namak.config;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.namak.services.GenAIService;

import reactor.core.publisher.Flux;

@Configuration
public class DataInitializerConfig {
    @Bean
	@ConditionalOnProperty(name = "questions.load-init", havingValue = "true")
	public
	CommandLineRunner initQuestionsGen(GenAIService genAIService, ResourcePatternResolver resourcePatternResolver) {
		return args -> {
			try {
				AtomicLong totalTime = new AtomicLong(0);
				AtomicLong questions = new AtomicLong(0);
				Resource[] resources = resourcePatternResolver.getResources("classpath:docs/**");
				Flux.fromArray(resources)
						.filter(Resource::isReadable)
						.map(resource -> {
							try {
								return resource.getFilename();
							} catch (Exception e) {
								return null;
							}
						})
						.filter(name -> name != null && !name.isEmpty())
						.doOnNext(documentName -> System.out
								.println("Generating and saving questions from document: " + documentName))
						.flatMap(documentName -> {
							long fileStartTime = System.currentTimeMillis();
							return genAIService.generateAndSaveQuestionsFromDocument("docs/" + documentName)
									.doOnTerminate(() -> {
										long fileEndTime = System.currentTimeMillis();
										long duration = fileEndTime - fileStartTime;
										totalTime.addAndGet(duration);
										System.out.println("Time to load " + documentName + ": " + duration + "ms");
									});
						})
						.doOnNext(question -> questions.addAndGet(1))
						.doOnComplete(
								() -> {
									System.out.println("Questions have been saved to DB: " + questions.get());
									System.out.println("Total time to load all files: " + totalTime.get() + "ms");
								})
						.subscribe();
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
	}
}
