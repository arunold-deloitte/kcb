package com.namak.repositories;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.namak.models.Submission;
import reactor.core.publisher.Flux;

public interface SubmissionRepository extends FirestoreReactiveRepository<Submission> {
    Flux<Submission> findByParticipantId(String participantId);
}
