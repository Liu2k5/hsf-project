package com.hsf.hsf_project.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Trang chủ chung
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Chào mừng đến với trang chủ!");
        return "customer/customerHomePage"; // Giao diện cho khách hoặc chưa đăng nhập
    }

    // Trang admin
    @GetMapping("/admin")
    public String adminHome(Model model, Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : "Guest";
        model.addAttribute("adminName", username);

        if (authentication == null ||
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/access-denied";
        }

        return "admin/adminHomePage";
    }

    // Trang customer
    @GetMapping("/customer")
    public String customerHome(Model model, Authentication authentication) {
        if (authentication == null ||
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"))) {
            return "redirect:/access-denied";
        }
        String username = (authentication != null) ? authentication.getName() : "Guest";
        model.addAttribute("customerName", username);
        return "customer/customerHomePage";
    }

    // Trang lỗi
    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("error", "Bạn không có quyền truy cập vào trang này!");
        return "error/accessDenied";
    }
}
