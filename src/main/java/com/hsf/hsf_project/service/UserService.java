package com.hsf.hsf_project.service;

import org.springframework.stereotype.Service;

import com.hsf.hsf_project.entity.Users;
import com.hsf.hsf_project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Users findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
