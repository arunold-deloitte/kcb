package com.namak.services;

import java.util.List;
import java.util.concurrent.ExecutionException;

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
    private final QuestionRepository questionRepository;
    private final FirestoreTemplate firestoreTemplate;
    private final Firestore db;

    public QuestionService(QuestionRepository questionRepository, FirestoreTemplate firestoreTemplate, Firestore db) {
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
        Query query = questionsRef.select("sop", "lob", "question", "options/option").whereEqualTo("lob", lob)
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
}
