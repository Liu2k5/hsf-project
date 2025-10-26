package com.hsf.hsf_project.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    private String[] resourceUrls = {"/css/**", "/js/**", "/images/**"};

    private String[] guestPages = {"/", "/details", "/login", "/signup", "/api/license/**"};

    private String[] adminPages = {"/admin/**"};

    private String[] customerPages = {"/customer/**"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            .requestMatchers(resourceUrls).permitAll()
                .requestMatchers(guestPages).permitAll()
                .requestMatchers(customerPages).hasAuthority("customer")
                .requestMatchers(adminPages).hasAuthority("admin")
                .anyRequest().authenticated()
            )
            // .formLogin(Customizer.withDefaults())
            .formLogin(form ->form.loginPage("/login").permitAll())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            var authorities = authentication.getAuthorities();
            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("admin"))) {
                response.sendRedirect("/admin/");
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("customer"))) {
                response.sendRedirect("/customer/");
            } else {
                response.sendRedirect("/");
            }
        };
    }



}