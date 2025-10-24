package com.namak.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.namak.models.Question;
import com.namak.models.Submission;
import com.namak.repositories.QuestionRepository;
import com.namak.repositories.SubmissionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public Mono<Submission> submit(Submission submission) {
        List<String> questionIds = submission.getAnswers().stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        return questionRepository.findAllById(questionIds)
                .collectMap(Question::getId, q -> q)
                .flatMap(correctQuestionsMap -> {
                    submission.getAnswers().forEach(q -> {
                        Question correctQuestion = correctQuestionsMap.get(q.getId());
                        if (correctQuestion != null) {
                            q.setAnswer(correctQuestion.getAnswer());
                        }
                    });

                    long correctCount = submission.getAnswers().stream()
                            .filter(q -> {
                                Question correctQuestion = correctQuestionsMap.get(q.getId());
                                return correctQuestion != null && correctQuestion.getAnswer().equals(q.getSelectedOption());
                            })
                            .count();

                    double score = (double) correctCount / submission.getAnswers().size() * 100;
                    submission.setScore(score);

                    return submissionRepository.save(submission);
                });
    }

    public Flux<Submission> getSubmissionsByParticipantId(String participantId) {
        return submissionRepository.findByParticipantId(participantId);
    }
}
