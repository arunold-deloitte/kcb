package com.namak.services;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import com.namak.models.Question;
import com.namak.repositories.QuestionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class QuestionService {
    private final ResourcePatternResolver resourcePatternResolver;
    private final GenAIService genAIService;
    private final QuestionRepository questionRepository;
    private final FirestoreTemplate firestoreTemplate;
    private final Firestore db;

    public QuestionService(ResourcePatternResolver resourcePatternResolver, GenAIService genAIService,
            QuestionRepository questionRepository, FirestoreTemplate firestoreTemplate, Firestore db) {
        this.resourcePatternResolver = resourcePatternResolver;
        this.genAIService = genAIService;
        this.questionRepository = questionRepository;
        this.firestoreTemplate = firestoreTemplate;
        this.db = db;
    }

    public Flux<Question> getQuestions(int count) {
        CollectionReference questionsRef = db.collection("questions");
        Query query = questionsRef.limit(count);
        try {
            return Flux.fromIterable(query.get().get().toObjects(Question.class));
        } catch (InterruptedException | ExecutionException e) {
            return Flux.error(new RuntimeException("Failed to fetch data from DB: ", e));
        }
    }

    public Mono<List<String>> getLobs() {
        CollectionReference questionsRef = db.collection("questions");
        Query query = questionsRef.select("lob");
        try {
            return Flux.fromIterable(query.get().get().toObjects(Question.class)).map(Question::getLob).distinct()
                    .collectList();
        } catch (InterruptedException | ExecutionException e) {
            return Mono.error(new RuntimeException("Failed to fetch data from DB: ", e));
        }
    }

    public Mono<List<String>> getSopByLob(String lob) {
        CollectionReference questionsRef = db.collection("questions");
        Query query = questionsRef.select("sop").whereEqualTo("lob", lob);
        try {
            return Flux.fromIterable(query.get().get().toObjects(Question.class)).map(q -> q.getSop()).distinct()
                    .collectList();
        } catch (InterruptedException | ExecutionException e) {
            return Mono.error(new RuntimeException("Failed to fetch data from DB: ", e));
        }
    }

    public Flux<Question> getQuestionsByLobAndSopAndCount(String lob, String sop, int count) {
        CollectionReference questionsRef = db.collection("questions");
        Query query = questionsRef.select("sop", "lob", "question", "options").whereEqualTo("lob", lob)
                .whereEqualTo("sop", sop).limit(count);
        try {
            return Flux.fromIterable(query.get().get().toObjects(Question.class));
        } catch (InterruptedException | ExecutionException e) {
            return Flux.error(new RuntimeException("Failed to fetch data from DB: ", e));
        }
    }

    public void clearAllQuestions() {
        questionRepository.deleteAll().subscribe();
    }

    public String loadQuestionsFromDocs(String docsPath) {
        try {
            StringBuffer out = new StringBuffer();
            AtomicLong totalTime = new AtomicLong(0);
            AtomicLong questions = new AtomicLong(0);
            Resource[] resources = resourcePatternResolver.getResources("file:" + docsPath + "/**");
            Flux.fromArray(resources)
                    .filter(Resource::isReadable)
                    .map(resource -> {
                        try {
                            return resource.getFile().getAbsolutePath();
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(absoluteFilePath -> absoluteFilePath != null && !absoluteFilePath.isEmpty())
                    .doOnNext(absoluteFilePath -> System.out
                            .println("Generating and saving questions from document: " + absoluteFilePath))
                    .flatMap(absoluteFilePath -> {
                        long fileStartTime = System.currentTimeMillis();
                        return genAIService.generateAndSaveQuestionsFromDocument(absoluteFilePath)
                                .doOnTerminate(() -> {
                                    long fileEndTime = System.currentTimeMillis();
                                    long duration = fileEndTime - fileStartTime;
                                    totalTime.addAndGet(duration);
                                    out.append("Time to load " + absoluteFilePath + ": " + duration + "ms\n");
                                    System.out.println("Time to load " + absoluteFilePath + ": " + duration + "ms");
                                });
                    })
                    .doOnNext(question -> questions.addAndGet(1))
                    .doOnComplete(
                            () -> {
                                out.append("Questions have been saved to DB: " + questions.get() + "\n");
                                out.append("Total time to load all files: " + totalTime.get() + "ms\n");
                                System.out.println("Questions have been saved to DB: " + questions.get());
                                System.out.println("Total time to load all files: " + totalTime.get() + "ms");
                            })
                    .subscribe();
                    return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
