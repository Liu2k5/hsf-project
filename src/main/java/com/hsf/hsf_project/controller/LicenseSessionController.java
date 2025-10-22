package com.hsf.hsf_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hsf.hsf_project.service.LicenseSessionService;

@RestController
@RequestMapping("/api/license")
public class LicenseSessionController {

    @Autowired
    private LicenseSessionService sessionService;

    @PostMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam String licenseKey,
                                           @RequestParam String deviceId) {
        return ResponseEntity.ok(sessionService.activateLicense(licenseKey, deviceId));
    }

    @DeleteMapping("/deactivate")
    public ResponseEntity<String> deactivate(@RequestParam String licenseKey,
                                             @RequestParam String deviceId) {
        sessionService.deactivateLicense(licenseKey, deviceId);
        return ResponseEntity.ok("License deactivated");
    }

    @PatchMapping("/heartbeat")
    public ResponseEntity<String> heartbeat(@RequestParam String licenseKey,
                                            @RequestParam String deviceId) {
        sessionService.heartbeat(licenseKey, deviceId);
        return ResponseEntity.ok("Heartbeat received");
    }
}
