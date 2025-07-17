package com.cmpdi.project.controller;



import com.cmpdi.project.dto.QuestionDTO;
import com.cmpdi.project.model.Question;
import com.cmpdi.project.service.QuestionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizApiController {

  
    
    @Autowired
    private QuestionService questionService;
    
    @GetMapping("/{quizId}/question/{index}")
    public ResponseEntity<QuestionDTO> getQuestion(@PathVariable String quizId, @PathVariable int index) {
        List<Question> questions = questionService.getDecryptedQuestionsByQuizId(quizId);

        if (index < 0 || index >= questions.size()) {
            return ResponseEntity.badRequest().build(); // returns 400 Bad Request
        }

        Question q = questions.get(index);

        List<String> options = q.getAnswers().stream()
                .map(answer -> answer.getText())
                .toList();
        
        boolean multipleCorrect = q.getAnswers().stream()
                .filter(a -> a.isCorrect())
                .count() > 1;

        QuestionDTO dto = new QuestionDTO();
        dto.setId(q.getQuestionId());
        dto.setText(q.getText());
        dto.setType(q.getType());
        dto.setOptions(options);
        dto.setMultipleCorrect(multipleCorrect); 

        return ResponseEntity.ok(dto);
    }


}
