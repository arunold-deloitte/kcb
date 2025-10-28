package com.namak.services;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.namak.models.Question;
import com.namak.repositories.QuestionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class QuestionService {
    private final ResourcePatternResolver resourcePatternResolver;
    private final GenAIService genAIService;
    private final QuestionRepository questionRepository;
    private final Firestore db;

    @Value("${namak.default.questions.count:10}")
    int defaultQuestionsCount;

    public QuestionService(ResourcePatternResolver resourcePatternResolver, GenAIService genAIService,
            QuestionRepository questionRepository, Firestore db) {
        this.resourcePatternResolver = resourcePatternResolver;
        this.genAIService = genAIService;
        this.questionRepository = questionRepository;
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
        Query query = questionsRef.select("question", "options").whereEqualTo("lob", lob)
                .whereEqualTo("sop", sop).orderBy("lastRetrieved").limit(count);
        try {
            return Flux.fromIterable(query.get().get().toObjects(Question.class));
        } catch (InterruptedException | ExecutionException e) {
            return Flux.error(new RuntimeException("Failed to fetch data from DB: ", e));
        }
    }

    public Flux<Question> getQuestionsByLob1(String lob) {
        CollectionReference questionsRef = db.collection("questions");
        Query query = questionsRef.select("question", "options").whereEqualTo("lob", lob)
                .limit(defaultQuestionsCount);
        try {
            return Flux.fromIterable(query.get().get().toObjects(Question.class));
        } catch (InterruptedException | ExecutionException e) {
            return Flux.error(new RuntimeException("Failed to fetch data from DB: ", e));
        }
    }

    public Flux<Question> getQuestionsByLob(String lob) {
        Flux<Question> questions = getSopByLob(lob)
                .flatMapMany(sops -> {
                    if (sops.isEmpty())
                        return Flux.empty();

                    int numberOfSops = sops.size();
                    int questionsPerSop = defaultQuestionsCount / numberOfSops;
                    int remainder = defaultQuestionsCount % numberOfSops;

                    return Flux.fromIterable(sops)
                            .index()
                            .flatMap(indexedSop -> {
                                long index = indexedSop.getT1();
                                String sop = indexedSop.getT2();
                                int limit = questionsPerSop + (index < remainder ? 1 : 0);
                                if (limit == 0)
                                    return Flux.empty();
                                return getQuestionsByLobAndSopAndCount(lob, sop, limit);
                            });
                });
        return questions.collectList().map(list -> {
            updateLastRetrievedForQuestions(list);
            return list;
        }).flatMapMany(Flux::fromIterable);
    }

    public void clearAllQuestions() {
        questionRepository.deleteAll().subscribe();
    }

    public Mono<String> loadQuestionsFromDocs(String docsPath) {
        long startTime = System.currentTimeMillis();

        return Mono.fromCallable(() -> resourcePatternResolver.getResources("file:" + docsPath + "/**"))
                .flatMapMany(Flux::fromArray)
                .filter(r -> r.isReadable() && r.getFilename().endsWith(".docx"))
                .map(resource -> {
                    try {
                        return resource.getFile().getAbsolutePath();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to get file path from resource", e);
                    }
                })
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(path -> {
                    System.out.println("Generating and saving questions from document: " + path);
                    long fileStartTime = System.currentTimeMillis();
                    return genAIService.generateQuestionsFromDocument(path)
                            .collectList()
                            .flatMap(questions -> {
                                long fileEndTime = System.currentTimeMillis();
                                long duration = (fileEndTime - fileStartTime) / 1000;
                                System.out.println("Generated " + questions.size() + " questions from " + path + " in "
                                        + duration + "s");
                                return questionRepository.saveAll(questions)
                                        .then(Mono.just(new FileProcessingResult(path, questions.size(), duration)));
                            });
                })
                .sequential()
                .collectList()
                .map(results -> {
                    long totalQuestions = results.stream().mapToLong(FileProcessingResult::questionCount).sum();
                    long totalDuration = System.currentTimeMillis() - startTime;
                    return "Successfully loaded " + totalQuestions + " questions from " + results.size() + " files in "
                            + totalDuration / 1000 + "s";
                })
                .doOnSubscribe(subscription -> clearAllQuestions());
    }

    private void updateLastRetrieved(List<Question> questions) {
        questions.forEach(q -> q.setLastRetrieved(System.currentTimeMillis()));
        questionRepository.saveAll(questions).subscribeOn(Schedulers.boundedElastic()).subscribe();
    }

    private void updateLastRetrievedForQuestions(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            return;
        }
        System.out.println("Updating lastRetrieved for " + questions.size() + " questions");
        Mono.fromRunnable(() -> {
            com.google.cloud.firestore.WriteBatch batch = db.batch();
            questions.forEach(q -> {
                com.google.cloud.firestore.DocumentReference docRef = db.collection("questions").document(q.getId());
                batch.update(docRef, "lastRetrieved", System.currentTimeMillis());
            });
            batch.commit();
            System.out.println("Updated lastRetrieved for " + questions.size() + " questions");
        }).subscribeOn(Schedulers.boundedElastic()).subscribe();
    }

    private record FileProcessingResult(String filePath, int questionCount, long duration) {
    }
}