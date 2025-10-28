package com.hsf.hsf_project.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.hsf.hsf_project.entity.Users;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Đảm bảo role luôn có tiền tố ROLE_ để Spring hiểu đúng
        String roleName = user.getRole().getRoleName();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName.toUpperCase();
        }

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(roleName.replace("ROLE_", "")) // roles() tự thêm ROLE_ lại
                .build();
    }
}
