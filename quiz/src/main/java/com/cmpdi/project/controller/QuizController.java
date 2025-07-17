package com.cmpdi.project.controller;



import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.cmpdi.project.model.Quiz;

import com.cmpdi.project.service.QuizService;






@Controller
@RequestMapping("/admin")
public class QuizController {
	
	@Autowired
    private QuizService quizService;
	

 
	
	@GetMapping("/create-quiz")
    public String showCreateQuizForm(Model model) {
        model.addAttribute("quiz", new Quiz());
        return "admin/create-quiz";
    }

	@PostMapping("/create-quiz")
	public String createQuiz(
	        @ModelAttribute Quiz quiz,
	        @RequestParam(value = "imageUrl", required = false) String imageUrl,

	        RedirectAttributes redirectAttributes) {

	    try {
	        
	    	if (imageUrl != null && !imageUrl.isBlank()) {
	            quiz.setImageUrl(imageUrl);
	        }
	    	
	    	quiz.setTotalMarks(quiz.getMarksPerQuestion() * quiz.getNumberOfQuestions());

	        Quiz savedQuiz = quizService.createQuiz(quiz);
	        
	        redirectAttributes.addFlashAttribute("message", "Quiz created successfully!");
	        return "redirect:/admin/add-questions/" + savedQuiz.getQuizId();

	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	        return "redirect:/admin/create-quiz";

	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "An unexpected error occurred: " + e.getMessage());
	        return "redirect:/admin/create-quiz";
	    }
	}
	
	@GetMapping("/edit-quiz-details/{quizId}")
    public String manageQuiz(@PathVariable String quizId, Model model) {
        Quiz quiz = quizService.getQuizByQuizId(quizId);
        //List<Question> questions = quizService.getDecryptedQuestionsByQuizId(quizId);
        model.addAttribute("quiz", quiz);
        //model.addAttribute("questions", questions);
        return "admin/edit-quiz-details";
    }

    // ✅ NEW: Save updated quiz metadata
	@PostMapping("/edit-quiz-details/{quizId}")
	public String updateQuiz(
	        @PathVariable String quizId,
	        @ModelAttribute Quiz updatedQuiz,
	        @RequestParam(value = "imageUrl", required = false) String imageUrl,
	        RedirectAttributes redirectAttributes) {

	    try {
	        // Optional image update
	        if (imageUrl != null && !imageUrl.isBlank()) {
	            updatedQuiz.setImageUrl(imageUrl);
	        }

	       
	        

	        // Ensure quizId is set (in case form didn't bind it)
	        updatedQuiz.setQuizId(quizId);

	        // Perform update
	        quizService.updateQuiz(quizId, updatedQuiz);

	       
	      
	        return "redirect:/admin/manage-questions/" + quizId;
	        

	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "Failed to update quiz: " + e.getMessage());
	        return "redirect:/admin/manage-questions/" + quizId;
	    }
	}


	@PostMapping("/delete-quiz/{quizId}")
	public String deleteQuiz(@PathVariable String quizId, RedirectAttributes redirect) {
	    boolean success = quizService.deleteQuizById(quizId);
	    
	    if (success) {
	        redirect.addFlashAttribute("message", "Quiz deleted successfully.");
	    } else {
	        redirect.addFlashAttribute("error", "Failed to delete quiz.");
	    }
	    
	    return "redirect:/admin/dashboard";
	}




}
