package com.cmpdi.project.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cmpdi.project.dto.AnswerDTO;
import com.cmpdi.project.model.MasterEmployee;
import com.cmpdi.project.model.Question;
import com.cmpdi.project.model.Quiz;
import com.cmpdi.project.model.User;
import com.cmpdi.project.service.AnswerSubmissionService;
import com.cmpdi.project.service.QuizAttemptService;
import com.cmpdi.project.service.QuizService;
import com.cmpdi.project.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/quiz")
public class QuizParticipationController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private AnswerSubmissionService answerSubmissionService;
    
    @Autowired
    private QuizAttemptService quizAttemptService;
    
    

    // Show Quiz Details Page
    @GetMapping("/{quizId}/details")
    public String showQuizDetails(@PathVariable String quizId, 
                                  Principal principal, 
                                  HttpSession session, 
                                  Model model, 
                                  RedirectAttributes redirectAttributes) {

        String role;
        if (principal != null) {
            User user = userService.getUserByEmail(principal.getName());
            role = "USER";
        } else if (session.getAttribute("employee") != null) {
            role = "EMPLOYEE";
        } else {
            redirectAttributes.addFlashAttribute("error", "Please login to view quiz.");
            return "redirect:/";
        }

        Quiz quiz = quizService.getQuizByQuizId(quizId);
        if (quiz == null) {
            redirectAttributes.addFlashAttribute("error", "Quiz not found.");
            return "redirect:/";
        }

        if (quiz.getType().equalsIgnoreCase("EMPLOYEE") && !role.equals("EMPLOYEE")) {
            redirectAttributes.addFlashAttribute("error", "This quiz is only for employees.");
            return "redirect:/";
        }

        LocalDateTime now = LocalDateTime.now();
        boolean canAttempt = now.isAfter(quiz.getAttemptFrom()) && now.isBefore(quiz.getAttemptTo());

        model.addAttribute("quiz", quiz);
        model.addAttribute("canAttempt", canAttempt);
        model.addAttribute("quizId", quizId);

        return "quiz/quiz-details";
    }

    // Start Quiz Page
    @GetMapping("/{quizId}/start")
    public String startQuiz(@PathVariable String quizId,
                            Principal principal,
                            HttpSession session,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        String participantId;
        String role;

        if (principal != null) {
            User user = userService.getUserByEmail(principal.getName());
            participantId = user.getEmail();
            //System.out.println(participantId);
            role = "USER";
        } else if (session.getAttribute("employee") != null) {
            MasterEmployee emp = (MasterEmployee) session.getAttribute("employee");
            participantId = emp.getEmployeeId();
            role = "EMPLOYEE";
        } else {
            redirectAttributes.addFlashAttribute("error", "Please login to take quiz.");
            return "redirect:/";
        }

        Quiz quiz = quizService.getQuizByQuizId(quizId);
        if (quiz == null) {
            redirectAttributes.addFlashAttribute("error", "Quiz not found.");
            return "redirect:/";
        }

        if (quiz.getType().equalsIgnoreCase("EMPLOYEE") && !role.equals("EMPLOYEE")) {
            redirectAttributes.addFlashAttribute("error", "This quiz is only for employees.");
            return "redirect:/";
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(quiz.getAttemptFrom()) || now.isAfter(quiz.getAttemptTo())) {
            redirectAttributes.addFlashAttribute("error", "Quiz is not live at this moment.");
            return "redirect:/quiz/" + quizId + "/details";
        }

        List<Question> questions = quizService.getQuestionsByQuizId(quizId);
        if (questions == null || questions.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "This quiz has no questions.");
            return "redirect:/quiz/" + quizId + "/details";
        }

        model.addAttribute("quiz", quiz);
        model.addAttribute("duration", quiz.getDurationMinutes());
        model.addAttribute("quizId", quizId);
        model.addAttribute("totalQuestions", quiz.getNumberOfQuestions());

        return "quiz/take-quiz";
    }

    // Submit Quiz Endpoint
    @PostMapping("/submit")
    public String submitQuiz(@RequestParam String quizId,
                             Principal principal,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        String participantId;

        if (principal != null) {
            participantId = principal.getName();
        } else if (session.getAttribute("employee") != null) {
            participantId = ((MasterEmployee) session.getAttribute("employee")).getEmployeeId();
        } else {
            redirectAttributes.addFlashAttribute("error", "Not logged in");
            return "redirect:/";
        }

        int score = quizAttemptService.calculateScore(quizId, participantId);
        quizAttemptService.saveAttempt(quizId, participantId, score);

        redirectAttributes.addFlashAttribute("message", "Quiz submitted successfully!");
        return "redirect:/";
    }
    
    @PostMapping("/{quizId}/save-answer")
    public ResponseEntity<String> saveAnswer(@PathVariable String quizId,
                                             @RequestBody AnswerDTO answerDto,
                                             Principal principal,
                                             HttpSession session) {
        String participantId;

        if (principal != null) {
            participantId = principal.getName(); // ✅ Use email
        } else if (session.getAttribute("employee") != null) {
            participantId = ((MasterEmployee) session.getAttribute("employee")).getEmployeeId(); // ✅ already good
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        answerSubmissionService.saveAnswer(
            quizId,
            answerDto.getQuestionId(),
            participantId,
            answerDto.getSelectedAnswer()
        );

        return ResponseEntity.ok("Answer saved");
    }

}
