package com.cmpdi.project.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmpdi.project.model.Question;


public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuizId(String quizId);
    boolean existsByQuestionId(String questionId);
    long countByQuizId(String quizId);
   
    

}
