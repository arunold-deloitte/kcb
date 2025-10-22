package com.namak.models;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collectionName = "participants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    @DocumentId
    private String id;

    private String firstName;

    private String lastName;

    private String email;
}