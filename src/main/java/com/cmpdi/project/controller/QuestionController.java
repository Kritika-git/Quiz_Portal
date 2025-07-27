package com.cmpdi.project.controller;

import com.cmpdi.project.model.Answer;
import com.cmpdi.project.model.Question;
import com.cmpdi.project.model.Quiz;
import com.cmpdi.project.service.QuestionService;
import com.cmpdi.project.service.QuizService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizService quizService;

    // ✅ Show form to add questions to a specific quiz
    @GetMapping("/add-questions/{quizId}")
    public String showAddQuestionForm(@PathVariable String quizId, Model model) {
        long added = questionService.countQuestionsByQuizId(quizId);
        int required = quizService.getQuizByQuizId(quizId).getNumberOfQuestions();

        model.addAttribute("quizId", quizId);
        model.addAttribute("questionsAdded", added);
        model.addAttribute("questionsRemaining", required - added);
        model.addAttribute("isComplete", added >= required);

        return "admin/add-question-form";
    }

    // ✅ Handle form submission
    @PostMapping("/add-questions/{quizId}")
    public String saveQuestion(
            @PathVariable String quizId,
            @RequestParam("questionText") String questionText,
            @RequestParam(value = "mediaUrl", required = false) String mediaUrl,
            @RequestParam("type") String type,
            @RequestParam("options") List<String> options,
            @RequestParam(value = "correctOption", required = false) List<Integer> correctOptionIndexes,
            RedirectAttributes redirectAttributes
    ) {
        Question question = new Question();
        question.setQuizId(quizId);
        question.setText(questionText);
        question.setMediaUrl(mediaUrl);
        question.setType(type);  // ✅ Set type from form

        List<Answer> answers = new ArrayList<>();
        if (mediaUrl != null && !mediaUrl.isBlank()) {
            question.setMediaUrl(mediaUrl);
        }

        if (type.equals("TEXT")) {
            // For TEXT questions, assume first option is correct full-text answer
            if (options.get(0).trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please enter a valid text answer.");
                return "redirect:/admin/add-questions/" + quizId;
            }
            Answer ans = new Answer();
            ans.setText(options.get(0)); // Only one input field in form
            ans.setCorrect(true);
            answers.add(ans);
        } else {
            // For MCQ/multi-correct, require at least one correct option
            if (correctOptionIndexes == null || correctOptionIndexes.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select at least one correct answer.");
                return "redirect:/admin/add-questions/" + quizId;
            }

            for (int i = 0; i < options.size(); i++) {
                Answer ans = new Answer();
                ans.setText(options.get(i));
                ans.setCorrect(correctOptionIndexes.contains(i));
                answers.add(ans);
            }
        }

        questionService.addQuestionWithAnswers(question, answers);
        redirectAttributes.addFlashAttribute("message", "Question added successfully!");

        long added = questionService.countQuestionsByQuizId(quizId);
        int required = quizService.getQuizByQuizId(quizId).getNumberOfQuestions();

        if (added >= required) {
            return "redirect:/admin/preview-quiz/" + quizId;
        }
        return "redirect:/admin/add-questions/" + quizId;
    }


    // ✅ Preview page that decrypts options before displaying
    @GetMapping("/preview-quiz/{quizId}")
    public String previewQuiz(@PathVariable String quizId, Model model) {
        Quiz quiz = quizService.getQuizByQuizId(quizId);

        // Use decrypted version
        List<Question> questions = questionService.getDecryptedQuestionsByQuizId(quizId);

        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);

        return "admin/preview-quiz";
    }
    
 // 🆕 Manage Questions page (separate from editing quiz metadata)
    @GetMapping("/manage-questions/{quizId}")
    public String manageQuestions(@PathVariable String quizId, Model model) {
        Quiz quiz = quizService.getQuizByQuizId(quizId);
        List<Question> questions = quizService.getDecryptedQuestionsByQuizId(quizId);

        int current = questions.size();
        int expected = quiz.getNumberOfQuestions();
        int remaining = expected - current;

        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);
        model.addAttribute("remaining", remaining);

        return "admin/manage-questions";
    }
    
   

    // 🆕 Finalize & publish the quiz after adding all questions
    @PostMapping("/finalize-quiz")
    public String finalizeQuiz(@RequestParam String quizId, RedirectAttributes redirectAttributes) {
        try {
            quizService.publishQuiz(quizId);
            redirectAttributes.addFlashAttribute("message", "Quiz finalized and published!");
            System.out.println("Quiz pulished");
            return "redirect:/admin/dashboard" ;
        } catch (Exception e) {
        	System.out.println("Quiz not pulished");
            redirectAttributes.addFlashAttribute("error", "Failed to publish quiz: " + e.getMessage());
            return "redirect:/admin/preview-quiz/" + quizId;
        }
    }


}
