package com.hsf.hsf_project.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.hsf.hsf_project.component.LicenseWebSocketHandler;

@Configuration
@EnableWebSocketSecurity
public class WebSocketConfig implements WebSocketConfigurer {
    private final LicenseWebSocketHandler handler;

    public WebSocketConfig(LicenseWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/license")
                .setAllowedOrigins("*"); // production: lock origins
    }
}