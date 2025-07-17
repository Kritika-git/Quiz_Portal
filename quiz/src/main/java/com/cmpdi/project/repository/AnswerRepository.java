package com.cmpdi.project.repository;




import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmpdi.project.model.Answer;
import com.cmpdi.project.model.Question;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
	 List<Answer> findByQuestion(Question question);
}
