package com.hsf.hsf_project.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ActiveLicenseSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licenseKey;

    private String deviceId;

    // @ManyToOne(fetch= FetchType.EAGER)
    // @JoinColumn(name = "license_id")
    // private License license;

    public void setLastHeartbeat(LocalDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setLastHeartbeat'");
    }
}
