package com.hsf.hsf_project.component;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hsf.hsf_project.service.LicenseSessionService;

@Component
public class SessionCleanupTask {
    private final LicenseSessionService service;
    @Value("${app.session.timeout-minutes}")
    private long timeoutMinutes;

    public SessionCleanupTask(LicenseSessionService service) { this.service = service; }

    @Scheduled(fixedRateString = "${app.heartbeat.interval-seconds}000")
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(timeoutMinutes);
        service.cleanupStaleSessions(cutoff);
    }
}