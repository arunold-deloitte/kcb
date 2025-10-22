package com.namak.services;

import com.namak.models.Participant;
import com.namak.repositories.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    public Mono<Participant> registerParticipant(Participant participant) {
        return participantRepository.findByEmail(participant.getEmail())
                .switchIfEmpty(participantRepository.save(participant))
                .cast(Participant.class);
    }

    public Mono<Participant> getParticipantByEmail(String email) {
        return participantRepository.findByEmail(email);
    }

    public Mono<Participant> getParticipantById(String id) {
        return participantRepository.findById(id);
    }

    public Flux<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }
}
