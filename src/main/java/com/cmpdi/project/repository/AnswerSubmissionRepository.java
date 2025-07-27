package com.cmpdi.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmpdi.project.model.AnswerSubmission;


public interface AnswerSubmissionRepository extends JpaRepository<AnswerSubmission, Long> {
    boolean existsByQuizIdAndQuestionIdAndParticipantId(String quizId, String questionId, String participantId);
    Optional<AnswerSubmission> findByQuizIdAndQuestionIdAndParticipantId(String quizId, String questionId, String participantId);
    List<AnswerSubmission> findByQuizIdAndParticipantId(String quizId, String participantId);

}
