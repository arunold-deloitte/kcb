package com.namak.repositories;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.namak.models.Question;

public interface QuestionRepository extends FirestoreReactiveRepository<Question> {

}