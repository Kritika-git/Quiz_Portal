package com.cmpdi.project.service;

import com.cmpdi.project.model.Answer;
import com.cmpdi.project.model.Question;
import com.cmpdi.project.repository.AnswerRepository;
import com.cmpdi.project.repository.QuestionRepository;
import com.cmpdi.project.util.EncryptionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    public Question addQuestionWithAnswers(Question question, List<Answer> answers) {
        if (answers == null || answers.isEmpty()) {
            throw new IllegalArgumentException("Answers must not be empty");
        }
        
        question.setText(EncryptionUtil.encrypt(question.getText()));
        // Generate unique question ID
        question.setQuestionId(generateQuestionId(question.getQuizId()));
        

        // Save question first
        Question savedQuestion = questionRepository.save(question);

        // Encrypt and attach answers
        for (Answer answer : answers) {
            answer.setText(EncryptionUtil.encrypt(answer.getText()));
            answer.setQuestion(savedQuestion);
        }

        answerRepository.saveAll(answers);
        return savedQuestion;
    }

    public long countQuestionsByQuizId(String quizId) {
        return questionRepository.countByQuizId(quizId);
    }

    public List<Question> getQuestionsByQuizId(String quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    public List<Question> getDecryptedQuestionsByQuizId(String quizId) {
        List<Question> questions = questionRepository.findByQuizId(quizId);
        List<Question> decryptedQuestions = new ArrayList<>();

        for (Question q : questions) {
            Question qCopy = new Question();
            qCopy.setQuestionId(q.getQuestionId());
            qCopy.setQuizId(q.getQuizId());

            String decryptedQuestionText = EncryptionUtil.decrypt(q.getText());
            System.out.println("Original Encrypted Question: " + q.getText());
            System.out.println("Decrypted Question Text: " + decryptedQuestionText);
            qCopy.setText(decryptedQuestionText);

            qCopy.setType(q.getType());

            List<Answer> decryptedAnswers = new ArrayList<>();
            for (Answer a : q.getAnswers()) {
                Answer aCopy = new Answer();
                aCopy.setCorrect(a.isCorrect());

                String encryptedAnswer = a.getText();
                String decryptedAnswer = EncryptionUtil.decrypt(encryptedAnswer);

                System.out.println("Original Encrypted Answer: " + encryptedAnswer);
                System.out.println("Decrypted Answer Text: " + decryptedAnswer);

                aCopy.setText(decryptedAnswer);
                decryptedAnswers.add(aCopy);
            }

            qCopy.setAnswers(decryptedAnswers);
            decryptedQuestions.add(qCopy);
        }

        return decryptedQuestions;
    }


    private String generateQuestionId(String quizId) {
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // e.g. 20250627
        String uuid = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return quizId + "-Q" + datePart + "-" + uuid;
    }
}
