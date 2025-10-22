package com.namak.repositories;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.namak.models.Participant;
import reactor.core.publisher.Mono;

public interface ParticipantRepository extends FirestoreReactiveRepository<Participant> {
    Mono<Participant> findByEmail(String email);
}
