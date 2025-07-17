package com.cmpdi.project.model;

import java.time.LocalDateTime;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String quizId; // auto-generated, human-readable
    private String title;
    private String description;

    private LocalDateTime loginFrom;
    private LocalDateTime loginTo;

    private LocalDateTime attemptFrom;
    private LocalDateTime attemptTo;

    private String type; // "EMPLOYEE" or "OPEN"
    private String location;

    private int numberOfQuestions;
    private int durationMinutes;
    
    
    private boolean published;

   
    private String imageUrl;
    private int marksPerQuestion;
    private int totalMarks;
    private boolean resultPublished;

    
	public String getQuizId() {
		return quizId;
	}
	public void setQuizId(String quizId) {
		this.quizId = quizId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LocalDateTime getLoginFrom() {
		return loginFrom;
	}
	public void setLoginFrom(LocalDateTime loginFrom) {
		this.loginFrom = loginFrom;
	}
	public LocalDateTime getLoginTo() {
		return loginTo;
	}
	public void setLoginTo(LocalDateTime loginTo) {
		this.loginTo = loginTo;
	}
	public LocalDateTime getAttemptFrom() {
		return attemptFrom;
	}
	public void setAttemptFrom(LocalDateTime attemptFrom) {
		this.attemptFrom = attemptFrom;
	}
	public LocalDateTime getAttemptTo() {
		return attemptTo;
	}
	public void setAttemptTo(LocalDateTime attemptTo) {
		this.attemptTo = attemptTo;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getNumberOfQuestions() {
		return numberOfQuestions;
	}
	public void setNumberOfQuestions(int numberOfQuestions) {
		this.numberOfQuestions = numberOfQuestions;
	}
	public int getDurationMinutes() {
		return durationMinutes;
	}
	public void setDurationMinutes(int durationMinutes) {
		this.durationMinutes = durationMinutes;
	}
	public boolean isPublished() {
		return published;
	}
	public void setPublished(boolean published) {
		this.published = published;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public int getMarksPerQuestion() {
		return marksPerQuestion;
	}
	public void setMarksPerQuestion(int marksPerQuestion) {
		this.marksPerQuestion = marksPerQuestion;
	}
	public int getTotalMarks() {
		return totalMarks;
	}
	public void setTotalMarks(int totalMarks) {
		this.totalMarks = totalMarks;
	}
	public boolean isResultPublished() {
		return resultPublished;
	}
	public void setResultPublished(boolean resultPublished) {
		this.resultPublished = resultPublished;
	}
	
  
}
