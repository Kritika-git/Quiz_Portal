package com.cmpdi.project.controller;

import java.time.LocalDate;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cmpdi.project.model.MasterEmployee;
import com.cmpdi.project.service.UserService;
import com.cmpdi.project.repository.MasterEmployeeRepository;

import jakarta.servlet.http.HttpSession;

@Controller

public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private MasterEmployeeRepository empRepo;

  
    @GetMapping("auth/user/register")
    public String showRegisterPage() {
        return "auth/user/register"; // templates/auth/user/register.html
    }

    
    @PostMapping("auth/user/register")
    public String registerUser(@RequestParam String email,
                               @RequestParam String password,
                               RedirectAttributes redirect) {
        if (userService.existsByEmail(email)) {
            redirect.addFlashAttribute("error", "Email already exists.");
            return "redirect:/auth/user/register";
        }

        userService.register(email, password);
        redirect.addFlashAttribute("message", "Registration successful!");
        return "redirect:/auth/user/login";
    }

    // ✅ Render User Login Page
    @GetMapping("auth/user/login")
    public String showUserLoginPage() {
        return "auth/user/login"; // templates/auth/user/login.html
    }

    // ✅ Render Employee/Admin Login Page
    @GetMapping("auth/employee/login")
    public String showEmployeeLoginPage() {
        return "auth/employee/login"; // templates/auth/employee/login.html
    }

    
 // ✅ Handle Employee/Admin Login
    @PostMapping("auth/employee/login")
    public String employeeLogin(@RequestParam String jobId,
                                @RequestParam String dob,
                                @RequestParam String role,
                                HttpSession session,
                                RedirectAttributes redirect) {
        try {
        	LocalDate parsedDob = LocalDate.parse(dob);
            MasterEmployee emp = empRepo.findByEmployeeIdAndDob(jobId, parsedDob).orElse(null);

            if (emp == null) {
                redirect.addFlashAttribute("error", "Invalid Job ID or DOB.");
                return "redirect:/auth/employee/login";
            }

            // Check for role match
            if (role.equalsIgnoreCase("ADMIN") && !emp.isAdmin()) {
                redirect.addFlashAttribute("error", "You are not authorized as an Admin.");
                return "redirect:/auth/employee/login";
            }

            if (role.equalsIgnoreCase("EMPLOYEE") && emp.isAdmin()) {
                redirect.addFlashAttribute("error", "Please select 'Admin' to login as Admin.");
                return "redirect:/auth/employee/login";
            }

            // ✅ Authenticated successfully
            session.setAttribute("employee", emp);
            System.out.println("Logged in as: " + emp.getEmployeeId() + ", isAdmin=" + emp.isAdmin());

            if (emp.isAdmin()) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/";
            }

        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Invalid date format. Use ddMMyyyy.");
            return "redirect:/auth/employee/login";
        }
    }


    // ✅ Optional logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/user/login";
    }
}
