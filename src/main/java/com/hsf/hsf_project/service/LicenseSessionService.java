package com.hsf.hsf_project.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hsf.hsf_project.component.LicenseWebSocketManager;
import com.hsf.hsf_project.entity.ActiveLicenseSession;
import com.hsf.hsf_project.entity.License;
import com.hsf.hsf_project.repository.ActiveLicenseSessionRepository;
import com.hsf.hsf_project.repository.LicenseRepository;

import jakarta.transaction.Transactional;

@Service
public class LicenseSessionService {
    private final LicenseRepository licenseRepo;
    private final ActiveLicenseSessionRepository sessionRepo;
    private final LicenseWebSocketManager wsManager;

    public LicenseSessionService(LicenseRepository licenseRepo,
                                 ActiveLicenseSessionRepository sessionRepo,
                                 LicenseWebSocketManager wsManager) {
        this.licenseRepo = licenseRepo;
        this.sessionRepo = sessionRepo;
        this.wsManager = wsManager;
    }

    @Transactional
    public synchronized String activate(String licenseKey, String deviceId, String productName) {
        License license = licenseRepo.findByLicenseKey(licenseKey)
                .orElseThrow(() -> new RuntimeException("License not found"));
        if (!license.getOrder().getProduct().getProductName().equals(productName)) {
            throw new RuntimeException("Product name mismatch");
        }

        // if (license.getStatus() != LicenseStatus.ACTIVE) {
        if (!license.isEnabled()) {
            throw new RuntimeException("License not active");
        }

        Optional<ActiveLicenseSession> existing = sessionRepo.findByLicenseKey(licenseKey);
        if (existing.isPresent()) {
            ActiveLicenseSession session = existing.get();
            if (!session.getDeviceId().equals(deviceId)) {
                throw new RuntimeException("License already in use by another device");
            }
            // same device: refresh heartbeat
            session.setLastHeartbeat(LocalDateTime.now());
            sessionRepo.save(session);
            return "reconnected";
        }

        ActiveLicenseSession session = new ActiveLicenseSession();
        session.setLicense(license);
        session.setLicenseKey(licenseKey);
        session.setDeviceId(deviceId);
        session.setStartedAt(LocalDateTime.now());
        session.setLastHeartbeat(LocalDateTime.now());
        sessionRepo.save(session);

        return "activated";
    }

    @Transactional
    public void deactivate(String licenseKey, String deviceId) {
        sessionRepo.findByLicenseKey(licenseKey).ifPresent(session -> {
            if (session.getDeviceId().equals(deviceId)) {
                sessionRepo.delete(session);
            } else {
                throw new RuntimeException("Device mismatch");
            }
        });
    }

    @Transactional
    public void heartbeat(String licenseKey, String deviceId) {
        sessionRepo.findByLicenseKey(licenseKey).ifPresent(session -> {
            System.out.println(session.getDeviceId() + " vs " + deviceId + "\n\n\n\n\n\n\n\n\n\n");
            if (session.getDeviceId().equals(deviceId)) {
                session.setLastHeartbeat(LocalDateTime.now());
                sessionRepo.save(session);
                System.err.println("Heartbeat updated for license: " + licenseKey);
            }
        });
    }

    @Transactional
    public void revokeLicense(String licenseKey, String reason) {
        licenseRepo.findByLicenseKey(licenseKey).ifPresent(lic -> {
            // lic.setStatus(LicenseStatus.REVOKED);
            lic.setEnabled(false);
            licenseRepo.save(lic);
            // notify active session if any
            sessionRepo.findByLicenseKey(licenseKey).ifPresent(session -> {
                // push websocket LOCK
                wsManager.sendLockToLicense(licenseKey, reason);
                // remove session
                sessionRepo.delete(session);
            });
        });
    }

    @Transactional
    public void resetDevice(String licenseKey) {
        sessionRepo.findByLicenseKey(licenseKey).ifPresent(session -> {
            sessionRepo.delete(session);
        });
    }

    // cleanup method invoked by scheduler
    @Transactional
    public void cleanupStaleSessions(LocalDateTime cutoff) {
        sessionRepo.deleteAllByLastHeartbeatBefore(cutoff);
    }
}