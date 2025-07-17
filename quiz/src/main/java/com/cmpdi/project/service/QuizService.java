package com.cmpdi.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmpdi.project.model.Answer;
import com.cmpdi.project.model.Question;
import com.cmpdi.project.model.Quiz;

import com.cmpdi.project.repository.QuestionRepository;
import com.cmpdi.project.repository.QuizRepository;
import com.cmpdi.project.util.EncryptionUtil;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;
   
    
    
    @Transactional
    public Quiz createQuiz(Quiz quiz) {
        // Generate a unique Quiz ID
    	validateQuizTimeWindows(quiz); 
        String quizId = generateUniqueQuizId();
        quiz.setQuizId(quizId);
        
        // Save to DB
        return quizRepository.save(quiz);
    }

    private String generateUniqueQuizId() {
        String uuidPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String datePart = java.time.LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); 
        return "QUIZ-" + datePart + "-" + uuidPart;
    }

    public Quiz getQuizByQuizId(String quizId) {
        return quizRepository.findByQuizId(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public List<Question> getQuestionsByQuizId(String quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    public void markAsPublished(String quizId) {
        Quiz quiz = quizRepository.findByQuizId(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setPublished(true);
        quizRepository.save(quiz);
    }
    public List<Quiz> getLiveQuizzes() {
        LocalDateTime now = LocalDateTime.now();
        return quizRepository.findByPublishedTrueAndAttemptFromBeforeAndAttemptToAfterAndLoginFromBeforeAndLoginToAfter(now, now, now, now);
    }
    public List<Quiz> getCurrentQuizzes() {
    	LocalDateTime now = LocalDateTime.now();
        List<Quiz> current = quizRepository.findByPublishedTrueAndAttemptToAfter(now);
        
        return current;
    }

    public List<Quiz> getPreviousQuizzes() {
        LocalDateTime now = LocalDateTime.now();
        return quizRepository.findByPublishedTrueAndAttemptToBefore(now);
    }
 // 1. LIVE Open Quizzes
    public List<Quiz> getLiveOpenQuizzesOnly() {
        LocalDateTime now = LocalDateTime.now();
        return quizRepository.findByPublishedTrueAndTypeAndLoginFromBeforeAndLoginToAfterAndAttemptFromBeforeAndAttemptToAfter(
                "OPEN", now, now, now, now
        );
    }

    // 2. UPCOMING Open Quizzes
    public List<Quiz> getUpcomingOpenQuizzesOnly() {
        LocalDateTime now = LocalDateTime.now();
        return quizRepository.findByPublishedTrueAndTypeAndAttemptFromAfter("OPEN", now);
    }

    // 3. PREVIOUS Open Quizzes
    public List<Quiz> getPreviousOpenQuizzesOnly() {
        LocalDateTime now = LocalDateTime.now();
        return quizRepository.findByPublishedTrueAndTypeAndAttemptToBefore("OPEN", now);
    }

    private void validateQuizTimeWindows(Quiz quiz) {
        LocalDateTime now = LocalDateTime.now();

        // Null checks
        if (quiz.getLoginFrom() == null || quiz.getLoginTo() == null ||
            quiz.getAttemptFrom() == null || quiz.getAttemptTo() == null) {
            throw new IllegalArgumentException("All time fields must be filled");
        }

        // Time order validations
        if (quiz.getLoginFrom().isAfter(quiz.getLoginTo())) {
            throw new IllegalArgumentException("Login start time must be before login end time");
        }

        if (quiz.getAttemptFrom().isAfter(quiz.getAttemptTo())) {
            throw new IllegalArgumentException("Attempt start time must be before attempt end time");
        }

       
        // Future time check
        if (quiz.getLoginFrom().isBefore(now)) {
            throw new IllegalArgumentException("Login start time must be in the future");
        }
        if(quiz.getAttemptFrom().isBefore(now)) {
        	throw new IllegalArgumentException("Attempt start time must be in the future");
        }
        if (quiz.getLoginTo().isBefore(now)) {
            throw new IllegalArgumentException("Login To time must be in the future");
        }
        if(quiz.getAttemptTo().isBefore(now)) {
        	throw new IllegalArgumentException("Attempt To time must be in the future");
        }

        // Logical quiz attributes
        if (quiz.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }

        if (quiz.getNumberOfQuestions() <= 0) {
            throw new IllegalArgumentException("Number of questions must be greater than 0");
        }
    }



    public void publishQuiz(String quizId) {
        Quiz quiz = quizRepository.findByQuizId(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        //validateQuizTimeWindows(quiz);
        quiz.setPublished(true);
        quizRepository.save(quiz);
       
    }
    
    
    @Transactional
    public void updateQuiz(String quizId, Quiz updatedData) {
        Quiz existingQuiz = quizRepository.findByQuizId(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        existingQuiz.setTitle(updatedData.getTitle());
        existingQuiz.setDescription(updatedData.getDescription());
        existingQuiz.setImageUrl(updatedData.getImageUrl());
        existingQuiz.setLoginFrom(updatedData.getLoginFrom());
        existingQuiz.setLoginTo(updatedData.getLoginTo());
        existingQuiz.setAttemptFrom(updatedData.getAttemptFrom());
        existingQuiz.setAttemptTo(updatedData.getAttemptTo());
        existingQuiz.setType(updatedData.getType());
        existingQuiz.setLocation(updatedData.getLocation());
        existingQuiz.setNumberOfQuestions(updatedData.getNumberOfQuestions());
        existingQuiz.setDurationMinutes(updatedData.getDurationMinutes());

        // Optional: Validate updated time windows
        validateQuizTimeWindows(existingQuiz);

        quizRepository.save(existingQuiz);
    }

   
    public boolean deleteQuizById(String quizId) {
        Optional<Quiz> quizOpt = quizRepository.findByQuizId(quizId);
        if (quizOpt.isPresent()) {
            quizRepository.delete(quizOpt.get());
            return true;
        }
        return false;
    }

    
    public List<Question> getDecryptedQuestionsByQuizId(String quizId) {
        List<Question> questions = questionRepository.findByQuizId(quizId);
        List<Question> decryptedQuestions = new ArrayList<>();

        for (Question q : questions) {
            Question qCopy = new Question();
            qCopy.setQuestionId(q.getQuestionId());
            qCopy.setQuizId(q.getQuizId());
            qCopy.setText(q.getText());
            qCopy.setType(q.getType());

            List<Answer> decryptedAnswers = new ArrayList<>();

            for (Answer a : q.getAnswers()) {
                String decryptedText;
                try {
                    decryptedText = EncryptionUtil.decrypt(a.getText());
                } catch (Exception e) {
                    System.out.println("Decryption failed for: " + a.getText());
                    decryptedText = a.getText(); // fallback to encrypted if error
                }

                Answer aCopy = new Answer();
                aCopy.setCorrect(a.isCorrect());
                aCopy.setText(decryptedText);
                decryptedAnswers.add(aCopy);
            }

            qCopy.setAnswers(decryptedAnswers);
            decryptedQuestions.add(qCopy);
        }

        return decryptedQuestions;
    }




    public void markResultsPublished(String quizId) {
       Quiz quiz = quizRepository.findByQuizId(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        quiz.setResultPublished(true);
        quizRepository.save(quiz);
    }

}
