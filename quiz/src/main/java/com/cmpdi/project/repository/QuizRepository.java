package com.cmpdi.project.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmpdi.project.model.Quiz;



public interface QuizRepository extends JpaRepository<Quiz, Long> {
    boolean existsByQuizId(String quizId);
    Optional<Quiz> findByQuizId(String quizId);
    
    List<Quiz> findByPublishedTrueAndAttemptToAfter(LocalDateTime now);
    List<Quiz> findByPublishedTrueAndAttemptToBefore(LocalDateTime now);
    List<Quiz> findByPublishedTrueAndAttemptFromBeforeAndAttemptToAfterAndLoginFromBeforeAndLoginToAfter(
    	    LocalDateTime attemptFrom, LocalDateTime attemptTo, LocalDateTime loginFrom, LocalDateTime loginTo
    	);
    List<Quiz> findByPublishedTrueAndTypeAndLoginFromBeforeAndLoginToAfterAndAttemptFromBeforeAndAttemptToAfter(
            String type,
            LocalDateTime loginFrom,
            LocalDateTime loginTo,
            LocalDateTime attemptFrom,
            LocalDateTime attemptTo
    );

    List<Quiz> findByPublishedTrueAndTypeAndAttemptFromAfter(String type, LocalDateTime now);

    List<Quiz> findByPublishedTrueAndTypeAndAttemptToBefore(String type, LocalDateTime now);



}
