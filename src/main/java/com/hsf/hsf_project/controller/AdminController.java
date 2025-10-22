package com.hsf.hsf_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    
    @RequestMapping("")
    public String adminHome() {
        return "/admin/homepage";
    }
}
