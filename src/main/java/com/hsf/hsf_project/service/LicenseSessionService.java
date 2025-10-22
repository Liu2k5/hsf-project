package com.hsf.hsf_project.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hsf.hsf_project.entity.ActiveLicenseSession;
import com.hsf.hsf_project.repository.ActiveLicenseSessionRepository;

@Service
public class LicenseSessionService {
    @Autowired
    private ActiveLicenseSessionRepository sessionRepo;

    public String activateLicense(String key, String deviceId) {
        // License license = licenseRepo.findByLicenseKey(licenseKey);

        Optional<ActiveLicenseSession> existing = sessionRepo.findByLicenseKey(key);

        if (existing.isPresent()) {
            ActiveLicenseSession session = existing.get();
            if (!session.getDeviceId().equals(deviceId)) {
                throw new RuntimeException("License is already in use by another device");
            }
            // same device re-login
            session.setLastHeartbeat(LocalDateTime.now());
            sessionRepo.save(session);
            return "Reconnected existing session";
        }

        ActiveLicenseSession newSession = new ActiveLicenseSession();
        // newSession.setLicense(license);
        newSession.setLicenseKey(key);
        newSession.setDeviceId(deviceId);
        sessionRepo.save(newSession);

        return "License activated successfully";
    }

    public void deactivateLicense(String key, String deviceId) {
        sessionRepo.findByLicenseKey(key).ifPresent(session -> {
            if (session.getDeviceId().equals(deviceId)) {
                sessionRepo.delete(session);
            } else {
                throw new RuntimeException("Device mismatch, cannot deactivate");
            }
        });
    }

    public void heartbeat(String key, String deviceId) {
        sessionRepo.findByLicenseKey(key).ifPresent(session -> {
            if (session.getDeviceId().equals(deviceId)) {
                session.setLastHeartbeat(LocalDateTime.now());
                sessionRepo.save(session);
            }
        });
    }
}
