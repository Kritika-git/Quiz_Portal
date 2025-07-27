package com.cmpdi.project.service;

import com.cmpdi.project.model.Answer;
import com.cmpdi.project.model.AnswerSubmission;
import com.cmpdi.project.model.Question;
import com.cmpdi.project.model.Quiz;
import com.cmpdi.project.model.QuizAttempt;
import com.cmpdi.project.repository.AnswerSubmissionRepository;
import com.cmpdi.project.repository.QuizAttemptRepository;
import com.cmpdi.project.util.EncryptionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuizAttemptService {

    @Autowired
    private AnswerSubmissionRepository answerRepo;

    @Autowired
    private QuizService quizService;
    
    @Autowired
    private QuizAttemptRepository quizAttemptRepo;

    

    public int calculateScore(String quizId, String participantId) {
        List<AnswerSubmission> answers = answerRepo.findByQuizIdAndParticipantId(quizId, participantId);
        List<Question> questions = quizService.getQuestionsByQuizId(quizId); // ⚠️ use unmodified version

        // Map<questionId, List<decrypted correct answers>>
        Map<String, List<String>> correctMap = new HashMap<>();
        for (Question q : questions) {
            List<String> correctAnswers = new ArrayList<>();
            for (Answer a : q.getAnswers()) {
                if (a.isCorrect()) {
                    String decrypted = EncryptionUtil.decrypt(a.getText()).trim().toLowerCase();
                    correctAnswers.add(decrypted);
                }
            }
            correctMap.put(q.getQuestionId(), correctAnswers);
        }

        int correctCount = 0;

        for (AnswerSubmission ans : answers) {
            List<String> correctAnswers = correctMap.get(ans.getQuestionId());
            if (correctAnswers == null) continue;

            String submittedRaw = ans.getSelectedAnswer();
            List<String> submittedAnswers;

            if (submittedRaw.contains("||")) {
                submittedAnswers = Arrays.stream(submittedRaw.split("\\|\\|"))
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .toList();

                if (new HashSet<>(submittedAnswers).equals(new HashSet<>(correctAnswers))) {
                    correctCount++;
                }
            } else {
                String submitted = submittedRaw.trim().toLowerCase();
                if (correctAnswers.stream().anyMatch(ansText -> ansText.equalsIgnoreCase(submitted))) {
                    correctCount++;
                }
            }
        }

        Quiz quiz = quizService.getQuizByQuizId(quizId);
        int marksPerQuestion = quiz.getMarksPerQuestion();
        return correctCount * marksPerQuestion;
    }
    
    public void saveAttempt(String quizId, String participantId, int score) {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuizId(quizId);
        attempt.setParticipantId(participantId);
        attempt.setScore(score);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setCompleted(true); 

        quizAttemptRepo.save(attempt);
    }
    public List<QuizAttempt> getQuizAttemptsSorted(String quizId) {
        return quizAttemptRepo.findByQuizIdOrderByScoreDesc(quizId);
    }
    public boolean isQuizCompleted(String participantId, String quizId) {
        QuizAttempt attempt = quizAttemptRepo.findByQuizIdAndParticipantId(quizId, participantId);
        return attempt != null && attempt.isCompleted();
    }


}
