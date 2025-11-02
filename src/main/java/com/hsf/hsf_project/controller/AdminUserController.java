package com.hsf.hsf_project.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hsf.hsf_project.entity.Role;
import com.hsf.hsf_project.entity.Users;
import com.hsf.hsf_project.repository.RoleRepository;
import com.hsf.hsf_project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    @GetMapping("")
    public String viewUsers(@RequestParam(required = false) Integer roleId, Model model) {
        List<Users> users;
        
        if (roleId != null) {
            users = userRepository.findByRole_RoleId(roleId);
        } else {
            users = userRepository.findAll();
        }
        
        List<Role> roles = roleRepository.findAll();
        
        model.addAttribute("users", users);
        model.addAttribute("roles", roles);
        model.addAttribute("selectedRoleId", roleId);
        
        return "/admin/users/list";
    }
    
    @PostMapping("/{id}/update-role")
    public String updateUserRole(@PathVariable Long id, @RequestParam Integer roleId) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role Id:" + roleId));
        user.setRole(role);
        userRepository.save(user);
        return "redirect:/admin/users";
    }
    
    @PostMapping("/{id}/toggle-lock")
    public String toggleUserLock(@PathVariable Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setLocked(!user.isLocked());
        userRepository.save(user);
        return "redirect:/admin/users";
    }
}
