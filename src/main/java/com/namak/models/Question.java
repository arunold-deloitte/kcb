package com.namak.models;

import java.util.List;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

@Document(collectionName = "questions")
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

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}