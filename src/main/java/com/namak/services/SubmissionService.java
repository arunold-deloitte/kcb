package com.namak.services;

import com.namak.models.Submission;
import com.namak.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    public Mono<Submission> submit(Submission submission) {
        // In a real application, you would calculate the score here
        // based on the provided answers.
        return submissionRepository.save(submission);
    }

    public Flux<Submission> getSubmissionsByParticipantId(String participantId) {
        return submissionRepository.findByParticipantId(participantId);
    }
}
