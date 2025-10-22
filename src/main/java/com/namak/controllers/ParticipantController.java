package com.namak.controllers;

import com.namak.models.Participant;
import com.namak.models.Submission;
import com.namak.services.ParticipantService;
import com.namak.services.SubmissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private SubmissionService submissionService;

    @PostMapping("/register")
    public Mono<Participant> register(@RequestBody Participant participant) {
        return participantService.registerParticipant(participant);
    }

    @GetMapping("/{email}")
    public Mono<Participant> getParticipant(@PathVariable String email) {
        return participantService.getParticipantByEmail(email);
    }

    @GetMapping
    public Flux<Participant> getAllParticipants() {
        return participantService.getAllParticipants();
    }

    @PostMapping("/submit")
    public Mono<Submission> submit(@RequestBody Submission submission) {
        return submissionService.submit(submission);
    }

    @GetMapping("/{participantId}/submissions")
    public Flux<Submission> getSubmissions(@PathVariable String participantId) {
        return submissionService.getSubmissionsByParticipantId(participantId);
    }
}
