package com.hsf.hsf_project.controller;

import com.hsf.hsf_project.entity.Users;
import com.hsf.hsf_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/guest")
@RequiredArgsConstructor
public class GuestController {

    private final UserService userService;

    // Hiển thị form đăng ký
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new Users());
        return "guest/signup";
    }

    // Xử lý form đăng ký
    @PostMapping("/signup")
    public String registerUser(@ModelAttribute("user") Users user, Model model) {
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Username already exists!");
            return "guest/signup";
        }

        userService.registerUser(user);
        model.addAttribute("success", "Account created successfully! Please login.");
        return "redirect:/guest/login";
    }

    // Hiển thị form đăng nhập
    @GetMapping("/login")
    public String showLoginForm() {
        return "guest/login";
    }
}
