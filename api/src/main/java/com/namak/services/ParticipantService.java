package com.namak.services;

import org.springframework.stereotype.Service;

import com.namak.models.Participant;
import com.namak.repositories.ParticipantRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

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
