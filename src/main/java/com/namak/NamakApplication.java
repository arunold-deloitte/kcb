package com.namak;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namak.services.ChatService;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class NamakApplication {

	public static void main(String[] args) {
		SpringApplication.run(NamakApplication.class, args);
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	@ConditionalOnProperty(name = "questions.load-init", havingValue = "true")
	CommandLineRunner commandLineRunner(ChatService chatService, ResourcePatternResolver resourcePatternResolver) {
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
							return chatService.generateAndSaveQuestionsFromDocument("docs/" + documentName)
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
