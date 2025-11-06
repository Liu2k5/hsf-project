package com.hsf.hsf_project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hsf.hsf_project.service.LicenseSessionService;

@RestController
@RequestMapping("/api/license")
public class LicenseSessionController {
    private final LicenseSessionService service;
    public LicenseSessionController(LicenseSessionService service) { this.service = service; }

    record ActivateRequest(String licenseKey, String deviceId, String productName) {}
    record GenericResp(String status, String message) {}

    @PostMapping("/activate")
    public ResponseEntity<?> activate(@RequestBody ActivateRequest req) {
        try {
            String r = service.activate(req.licenseKey(), req.deviceId(), req.productName());
            System.out.println("license key: " + req.licenseKey() + " activated for device: " + req.deviceId() + " product: " + req.productName());
            return ResponseEntity.ok(new GenericResp("OK", r));
        } catch (RuntimeException ex) {
            System.out.println("Activation failed: " + ex.getMessage());
            return ResponseEntity.status(409).body(new GenericResp("FAIL", ex.getMessage()));
        }
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivate(@RequestBody ActivateRequest req) {
        try {
            service.deactivate(req.licenseKey(), req.deviceId());
            return ResponseEntity.ok(new GenericResp("OK", "deactivated"));
        } catch (RuntimeException ex) {
            System.out.println("Deactivation failed: " + ex.getMessage());
            return ResponseEntity.status(400).body(new GenericResp("FAIL", ex.getMessage()));
        }
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<?> heartbeat(@RequestBody ActivateRequest req) {
        System.out.println("Heartbeat request received for license key: " + req.licenseKey() + " device: " + req.deviceId() + "\n\n\n\n\n");
        
        try {
            service.heartbeat(req.licenseKey(), req.deviceId());
            return ResponseEntity.ok(new GenericResp("OK", "heartbeat updated"));
        } catch (Exception e) {
            System.out.println("Heartbeat update failed: " + e.getMessage());
            return ResponseEntity.status(400).body(new GenericResp("FAIL", e.getMessage()));
        }
    }
}
