package com.hsf.hsf_project.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ActiveLicenseSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "license_id")
    private License license;

    private String licenseKey;

    private String deviceId;
    private LocalDateTime startedAt;
    private LocalDateTime lastHeartbeat;

    public void setLastHeartbeat(LocalDateTime now) {
        this.lastHeartbeat = now;
    }
}
