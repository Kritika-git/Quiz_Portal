package com.cmpdi.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmpdi.project.model.QuizAttempt;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    boolean existsByQuizIdAndParticipantId(String quizId, String participantId);
    QuizAttempt findByQuizIdAndParticipantId(String quizId, String participantId);
    List<QuizAttempt> findByQuizIdOrderByScoreDesc(String quizId);

}
