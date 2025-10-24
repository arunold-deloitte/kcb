package com.namak.models;

import java.util.List;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collectionName = "questions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @DocumentId
    private String id;

    private String question;

    private String answer;

    private List<String> options;

    private String sop;

    private String lob;

    private String selectedOption;

    private String description;
}