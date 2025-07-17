package com.cmpdi.project.service;



import com.cmpdi.project.model.AnswerSubmission;
import com.cmpdi.project.repository.AnswerSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AnswerSubmissionService {

    @Autowired
    private AnswerSubmissionRepository answerRepo;

    public void saveAnswer(String quizId, String questionId, String participantId, String selectedAnswer) {
        AnswerSubmission ans = new AnswerSubmission();
        ans.setQuizId(quizId);
        ans.setQuestionId(questionId);
        ans.setParticipantId(participantId);
        ans.setSelectedAnswer(selectedAnswer);
        ans.setSubmittedAt(LocalDateTime.now());

        answerRepo.save(ans); // ✅ Always save
    }

}
