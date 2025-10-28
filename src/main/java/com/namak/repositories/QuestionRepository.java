package com.namak.repositories;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.namak.models.Question;

import reactor.core.publisher.Flux;

public interface QuestionRepository extends FirestoreReactiveRepository<Question> {
    Flux<Question> findByLobAndSop(String lob, String sop);
    Flux<Question> findByLob(String lob);
}