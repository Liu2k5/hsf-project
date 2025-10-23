package com.hsf.hsf_project.component;

import org.springframework.stereotype.Component;

@Component
public class LicenseWebSocketManager {
    private final LicenseWebSocketHandler handler;
    public LicenseWebSocketManager(LicenseWebSocketHandler handler) {
        this.handler = handler;
    }

    public void sendLockToLicense(String licenseKey, String reason) {
        handler.sendLock(licenseKey, reason);
    }
}
