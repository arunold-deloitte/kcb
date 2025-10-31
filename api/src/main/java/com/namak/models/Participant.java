package com.namak.models;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Participant {

    @DocumentId
    private String id;

    private String firstName;

    private String lastName;

    private String email;
}