package com.cmpdi.project.dto;

import java.util.List;

public class QuestionDTO {
    private String id;
    private String text;
    private List<String> options;
    private String type;
    private boolean multipleCorrect;

    // Default constructor (required for serialization/deserialization)
    public QuestionDTO() {
    }

    // All-args constructor
    public QuestionDTO(String id, String text, String type, List<String> options, boolean multipleCorrect) {
        this.id = id;
        this.text = text;
        this.type = type;
        this.options = options;
        this.multipleCorrect = multipleCorrect;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getType() {
        return type;
    }

    public boolean isMultipleCorrect() {
        return multipleCorrect;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMultipleCorrect(boolean multipleCorrect) {
        this.multipleCorrect = multipleCorrect;
    }
}
