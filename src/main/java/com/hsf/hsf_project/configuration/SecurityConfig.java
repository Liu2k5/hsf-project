package com.hsf.hsf_project.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.hsf.hsf_project.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Cho phép truy cập không cần đăng nhập
                        .requestMatchers("/guest/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/api/license/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()  // Cho phép WebSocket
                        // Phân quyền
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/customer/**").hasRole("CUSTOMER")
                        // Mặc định
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                )
                .formLogin(form -> form
                        .loginPage("/guest/login")
                        .loginProcessingUrl("/process-login")
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureUrl("/guest/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/guest/login?logout=true")
                        .permitAll()
                )
                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            var authorities = authentication.getAuthorities();

            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                response.sendRedirect("/admin");
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
                response.sendRedirect("/customer");
            } else {
                response.sendRedirect("/");
            }
        };
    }
}
