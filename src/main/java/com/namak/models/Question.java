package com.namak.models;

import java.util.List;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

// import com.google.cloud.spring.data.firestore.mapping.Document;
// import com.google.cloud.spring.data.firestore.mapping.DocumentId;

@Document(collectionName = "questions")
public class Question {
    @DocumentId
    private String id;
    private String question;
    private String answer;
    private List<Option> options;
    private String sop;
    private String lob;
    private String selectedOption;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public String getSop() {
        return sop;
    }

    public void setSop(String sop) {
        this.sop = sop;
    }

    public String getLob() {
        return lob;
    }

    public void setLob(String lineOfBusiness) {
        this.lob = lineOfBusiness;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
}