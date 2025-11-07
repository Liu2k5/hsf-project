package com.hsf.hsf_project.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class RedirectController {

    @GetMapping("/")
    public void redirectRoot(Authentication authentication, HttpServletResponse response) throws IOException {
        if (authentication == null || !authentication.isAuthenticated()) {
            response.sendRedirect("/guest/login");
            return;
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));
        boolean isCustomer = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_CUSTOMER"));

        if (isAdmin) {
            response.sendRedirect("/admin#dashboard");
        } else if (isCustomer) {
            response.sendRedirect("/customer");
        } else {
            response.sendRedirect("/guest/login");
        }
    }
}
