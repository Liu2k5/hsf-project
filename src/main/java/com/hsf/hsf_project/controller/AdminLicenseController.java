package com.hsf.hsf_project.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hsf.hsf_project.entity.ActiveLicenseSession;
import com.hsf.hsf_project.entity.License;
import com.hsf.hsf_project.repository.ActiveLicenseSessionRepository;
import com.hsf.hsf_project.repository.LicenseRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/licenses")
public class AdminLicenseController {
    
    private final LicenseRepository licenseRepository;
    private final ActiveLicenseSessionRepository activeLicenseSessionRepository;
    
    @GetMapping("")
    public String viewLicenses(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long orderId,
            Model model) {
        
        List<License> licenses;
        
        if (userId != null) {
            licenses = licenseRepository.findByOrder_User_UserId(userId);
        } else if (orderId != null) {
            licenses = licenseRepository.findByOrder_OrderId(orderId);
        } else {
            licenses = licenseRepository.findAll();
        }
        
        model.addAttribute("licenses", licenses);
        model.addAttribute("userId", userId);
        model.addAttribute("orderId", orderId);
        
        return "/admin/licenses/list";
    }
    
    @PostMapping("/{id}/toggle-enable")
    public String toggleLicenseEnable(@PathVariable Long id) {
        License license = licenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid license Id:" + id));
        license.setEnabled(!license.isEnabled());
        licenseRepository.save(license);
        return "redirect:/admin/licenses";
    }
    
    @GetMapping("/sessions")
    public String viewActiveSessions(Model model) {
        List<ActiveLicenseSession> sessions = activeLicenseSessionRepository.findAll();
        model.addAttribute("sessions", sessions);
        return "/admin/licenses/sessions";
    }
}
