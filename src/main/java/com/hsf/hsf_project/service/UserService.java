package com.hsf.hsf_project.service;

import com.hsf.hsf_project.dto.RegisterRequest;
import com.hsf.hsf_project.dto.UserDTO;
import com.hsf.hsf_project.entity.Role;
import com.hsf.hsf_project.entity.Users;
import com.hsf.hsf_project.repository.RoleRepository;
import com.hsf.hsf_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return userRepository.existsByUsername(username);
    }

    public void registerUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role role = user.getRole();
        if (role == null) {
            role = roleRepository.findByRoleName(Role.ROLE_CUSTOMER);
            if (role == null) {
                role = new Role();
                role.setRoleName(Role.ROLE_CUSTOMER);
                roleRepository.save(role);
            }
        }

        user.setRole(role);
        userRepository.save(user);
    }

    public Page<UserDTO> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(user -> {
            UserDTO dto = new UserDTO();
            dto.setUserId(user.getUserId());
            dto.setUsername(user.getUsername());
            dto.setRoleName(user.getRole() != null ? user.getRole().getRoleName() : null);
            return dto;
        });
    }

    public void updateUserRole(Long userId, String newRoleName) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != null && Role.ROLE_ADMIN.equals(user.getRole().getRoleName())
                && Role.ROLE_CUSTOMER.equals(newRoleName)) {
            throw new IllegalArgumentException("Cannot demote admin to customer role");
        }

        Role role = roleRepository.findByRoleName(newRoleName);
        if (role == null) {
            throw new RuntimeException("Role not found");
        }
        user.setRole(role);
        userRepository.save(user);
    }

    public Users addUser(String username, String password, String email, String roleName) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        username = username.trim();

        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        email = email.trim();

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        Users user = new Users();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);

        Role role = roleRepository.findByRoleName(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Role not found: " + roleName);
        }
        user.setRole(role);

        return userRepository.save(user);
    }

    public void deleteUser(Long userId, String currentUsername) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != null && Role.ROLE_ADMIN.equals(user.getRole().getRoleName())) {
            throw new IllegalArgumentException("Cannot delete admin users");
        }

        if (user.getUsername().equals(currentUsername)) {
            throw new IllegalArgumentException("Cannot delete your own account");
        }

        userRepository.delete(user);
    }

    public Users registerUserFromDto(RegisterRequest req) {
        if (existsByUsername(req.getUsername())) throw new IllegalArgumentException("Username already exists");
        Users u = new Users();
        u.setUsername(req.getUsername().trim());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setEmail(req.getEmail().trim());
        Role role = roleRepository.findByRoleName(Role.ROLE_CUSTOMER);
        if (role == null) {
            role = new Role();
            role.setRoleName(Role.ROLE_CUSTOMER);
            roleRepository.save(role);
        }
        u.setRole(role);
        return userRepository.save(u);
    }
}
