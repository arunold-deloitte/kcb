package com.namak.models;

import java.util.List;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collectionName = "submissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Submission {

    @DocumentId
    private String id;

    private String participantId;

    private List<Question> answers; // List of maps, each map has "questionId", "answer"

    private Double score;

}

// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// class Answer {
//     private String questionId;
//     private String selectedOption;
// }