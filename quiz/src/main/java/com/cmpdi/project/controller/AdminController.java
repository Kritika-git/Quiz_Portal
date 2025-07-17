package com.cmpdi.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cmpdi.project.model.MasterEmployee;
import com.cmpdi.project.model.Quiz;
import com.cmpdi.project.model.QuizAttempt;
import com.cmpdi.project.service.MasterEmployeeService;
import com.cmpdi.project.service.QuizAttemptService;
import com.cmpdi.project.service.QuizService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MasterEmployeeService masterEmployeeService;

    @Autowired
    private QuizService quizService;
    
    @Autowired
    private QuizAttemptService quizAttemptService;


    // ✅ Protect dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session, RedirectAttributes redirect) {
        MasterEmployee emp = (MasterEmployee) session.getAttribute("employee");

        if (emp == null || !emp.isAdmin()) {
            redirect.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/auth/employee/login";
        }

        List<Quiz> currentQuizzes = quizService.getCurrentQuizzes();
        List<Quiz> previousQuizzes = quizService.getPreviousQuizzes();

        model.addAttribute("employees", masterEmployeeService.getAllEmployees());
        model.addAttribute("currentQuizzes", currentQuizzes);
        model.addAttribute("previousQuizzes", previousQuizzes);
        return "admin/admin-dashboard";
    }

    // ✅ Protect upload
    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        MasterEmployee emp = (MasterEmployee) session.getAttribute("employee");

        if (emp == null || !emp.isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/auth/employee/login";
        }

        try {
            masterEmployeeService.importFromExcel(file);
            redirectAttributes.addFlashAttribute("message", "Employees uploaded successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Upload failed: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    
    
    @GetMapping("/results/{quizId}")
    public String showResultsForAdmin(@PathVariable String quizId, Model model) {
        Quiz quiz = quizService.getQuizByQuizId(quizId);
        List<QuizAttempt> attempts = quizAttemptService.getQuizAttemptsSorted(quizId); // ranked by score

        model.addAttribute("quiz", quiz);
        model.addAttribute("attempts", attempts);
        return "admin/results-page";
    }
    @PostMapping("/publish-results/{quizId}")
    public String publishResults(@PathVariable String quizId, RedirectAttributes redirectAttributes) {
        quizService.markResultsPublished(quizId);
        redirectAttributes.addFlashAttribute("message", "Results published successfully!");
        return "redirect:/admin/results/" + quizId;
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Clears all session attributes
        return "redirect:/"; 
    }

}
