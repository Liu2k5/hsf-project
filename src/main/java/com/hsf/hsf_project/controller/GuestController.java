package com.hsf.hsf_project.controller;


import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hsf.hsf_project.entity.Product;
import com.hsf.hsf_project.entity.Users;
import com.hsf.hsf_project.service.ProductService;
import com.hsf.hsf_project.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class GuestController {
    private final ProductService productService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("")
    public String Homepage(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "/guest/homepage";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("err", "Sai tài khoản hoặc mật khẩu!");
        }
        return "guest/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password, 
                        Model model) {
        Users user = userService.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            try {
                Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
                );
                if (user.getRole().getRoleName().equals("admin")) {
                    return "redirect:/admin/";
                } else if (user.getRole().getRoleName().equals("customer")) {
                    return "redirect:/customer/";
                }
            } catch (Exception e) {
                model.addAttribute("err", "Đăng nhập không thành công!: " + e.getMessage());
            }
        } else {
            model.addAttribute("err", "Tên đăng nhập hoặc mật khẩu không đúng!");
        }
        return "/guest/login";
    }
    
    @GetMapping("/signup")
    public String signupPage() {
        return "/guest/signup";
    }

}
