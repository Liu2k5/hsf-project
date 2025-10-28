package com.hsf.hsf_project.service;

import com.hsf.hsf_project.entity.Role;
import com.hsf.hsf_project.entity.Users;
import com.hsf.hsf_project.repository.RoleRepository;
import com.hsf.hsf_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public Users findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public void registerUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role role = user.getRole();
        if (role == null) {
            role = roleRepository.findByRoleName("ROLE_CUSTOMER");
            if (role == null) {
                // Nếu database chưa có ROLE_CUSTOMER thì tự tạo
                role = new Role();
                role.setRoleName("ROLE_CUSTOMER");
                roleRepository.save(role);
            }
        }

        user.setRole(role);
        userRepository.save(user);
    }
}
