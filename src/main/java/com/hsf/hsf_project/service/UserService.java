package com.hsf.hsf_project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hsf.hsf_project.dto.UserDTO;
import com.hsf.hsf_project.entity.Role;
import com.hsf.hsf_project.entity.Users;
import com.hsf.hsf_project.repository.RoleRepository;
import com.hsf.hsf_project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Users findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Page<UserDTO> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(user -> {
            UserDTO dto = new UserDTO();
            dto.setUserId(user.getUserId());
            dto.setUsername(user.getUsername());
            dto.setRoleName(user.getRole() != null ? user.getRole().getRoleName() : null);
            dto.setLocked(user.isLocked());
            return dto;
        });
    }

    public void updateUserRole(Long userId, String newRoleName) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByRoleName(newRoleName);
        if (role == null) {
            throw new RuntimeException("Role not found");
        }
        user.setRole(role);
        userRepository.save(user);
    }

    public void toggleUserLock(Long userId, boolean lockStatus) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLocked(lockStatus);
        userRepository.save(user);
    }
}
