package com.hsf.hsf_project.controller;

import com.hsf.hsf_project.dto.RegisterRequest;
import com.hsf.hsf_project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/guest")
@RequiredArgsConstructor
public class GuestController {

    private final UserService userService;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("form", new RegisterRequest());
        return "guest/signup";
    }

    @PostMapping("/signup")
    public String registerUser(
            @Valid @ModelAttribute("form") RegisterRequest form,
            BindingResult br,
            Model model) {

        // Nếu có lỗi validation cơ bản
        if (br.hasErrors()) {
            return "guest/signup";
        }

        // Kiểm tra trùng mật khẩu
        if (!form.passwordsMatch()) {
            model.addAttribute("error", "Passwords do not match!");
            return "guest/signup";
        }

        // Kiểm tra username trùng
        if (userService.existsByUsername(form.getUsername())) {
            model.addAttribute("error", "Username already exists!");
            return "guest/signup";
        }

        try {
            userService.registerUserFromDto(form);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "guest/signup";
        }

        return "redirect:/guest/login?registered=true";
    }


    @GetMapping("/login")
    public String showLoginForm() {
        return "guest/login";
    }
}
