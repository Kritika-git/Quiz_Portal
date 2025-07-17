package com.cmpdi.project.controller;


import com.cmpdi.project.model.MasterEmployee;
import com.cmpdi.project.model.Quiz;
import com.cmpdi.project.model.QuizAttempt;
import com.cmpdi.project.model.User;
import com.cmpdi.project.service.QuizAttemptService;
import com.cmpdi.project.service.QuizService;
import com.cmpdi.project.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private QuizService quizService;
    
    @Autowired
    private QuizAttemptService quizAttemptService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String showHomePage(Model model, Principal principal, HttpSession session) {

        String role = "GUEST";
        String loginId = null;


        List<Quiz> liveQuizzes;
        List<Quiz> upcomingQuizzes;
        List<Quiz> previousQuizzes;

        if (principal != null) {
         
            User user = userService.getUserByEmail(principal.getName());
            loginId = user.getEmail();
            role = "USER";

            liveQuizzes = quizService.getLiveOpenQuizzesOnly();
            upcomingQuizzes = quizService.getUpcomingOpenQuizzesOnly();
            previousQuizzes = quizService.getPreviousOpenQuizzesOnly();

        } else if (session.getAttribute("employee") != null) {
            // Logged in as employee or admin (custom logic)
            MasterEmployee employee = (MasterEmployee) session.getAttribute("employee");
            loginId = employee.getEmployeeId();
            role = employee.isAdmin() ? "ADMIN" : "EMPLOYEE";

            liveQuizzes = quizService.getLiveQuizzes();
            upcomingQuizzes = quizService.getCurrentQuizzes();
            previousQuizzes = quizService.getPreviousQuizzes();

        } else {
            // Guest (not logged in)
            liveQuizzes = quizService.getLiveQuizzes();
            upcomingQuizzes = quizService.getCurrentQuizzes();
            previousQuizzes = quizService.getPreviousQuizzes();
        }
        
        final String userId = loginId;
        List<String> completedQuizIds = new ArrayList<>();
        if (loginId != null) {
            completedQuizIds = liveQuizzes.stream()
                    .filter(q -> quizAttemptService.isQuizCompleted(userId, q.getQuizId()))
                    .map(Quiz::getQuizId)
                    .collect(Collectors.toList());
        }
        model.addAttribute("role", role);
        model.addAttribute("loginId", loginId);
        model.addAttribute("liveQuizzes", liveQuizzes);
        model.addAttribute("upcomingQuizzes", upcomingQuizzes);
        model.addAttribute("previousQuizzes", previousQuizzes);
        model.addAttribute("completedQuizIds", completedQuizIds);
        
        return "public/home";
    }
    
    @GetMapping("/employee/logout")
    public String logoutEmployee(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    @GetMapping("quiz/see-results/{quizId}")
    public String seeResultsForUser(@PathVariable String quizId, Model model) {
        Quiz quiz = quizService.getQuizByQuizId(quizId);

        if (!quiz.isResultPublished()) {
            return "redirect:/home"; // or show error
        }

        List<QuizAttempt> attempts = quizAttemptService.getQuizAttemptsSorted(quizId);
        model.addAttribute("quiz", quiz);
        model.addAttribute("attempts", attempts);
        return "quiz/view-results";
    }

}
