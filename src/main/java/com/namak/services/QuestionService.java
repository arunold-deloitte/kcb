package com.namak.services;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${namak.default.questions.count:10}")
    int defaultQuestionsCount;

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

    public Flux<Question> getQuestionsByLob(String lob) {
        CollectionReference questionsRef = db.collection("questions");
        Query query = questionsRef.select("sop", "lob", "question", "options").whereEqualTo("lob", lob)
                .limit(defaultQuestionsCount);
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
            clearAllQuestions();
            StringBuffer out = new StringBuffer();
            AtomicLong totalTime = new AtomicLong(0);
            AtomicLong questionsCt = new AtomicLong(0);
            Resource[] resources = resourcePatternResolver.getResources("file:" + docsPath + "/**");
            Flux.fromArray(resources)
                    .filter(Resource::isReadable)
                    .map(resource -> {
                        try {
                            return resource.getFile().getAbsolutePath();
                        } catch (Exception e) {
                            return null;
                        }
                    }).filter(path -> path != null && !path.isEmpty())
                    .doOnNext(path -> System.out
                            .println("Generating and saving questions from document: " + path))
                    .flatMap(path -> {
                        long fileStartTime = System.currentTimeMillis();
                        return genAIService.generateQuestionsFromDocument(path)
                                .collectList().map(ques -> {
                                    questionsCt.addAndGet(ques.size());
                                    long fileEndTime = System.currentTimeMillis();
                                    long secs = (fileEndTime - fileStartTime) / 1000;
                                    totalTime.addAndGet(secs);
                                    String msg = "Questions: " + ques.size() + ", Time: " + secs + ", File: " + path;
                                    out.append(msg + "\n");
                                    System.out.println(msg);
                                    questionRepository.saveAll(ques).subscribe();
                                    return ques;
                                });
                    }).doOnComplete(
                            () -> {
                                out.append("Total questions saved to DB: " + questionsCt.get() + "\n");
                                out.append("Total time to load all files: " + totalTime.get() + "s\n");
                                System.out.println("Total questions saved to DB: " + questionsCt.get());
                                System.out.println("Total time to load all files: " + totalTime.get() + "s");
                            })
                    .subscribe();
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
