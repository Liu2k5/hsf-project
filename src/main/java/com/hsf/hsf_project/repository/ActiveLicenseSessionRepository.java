package com.hsf.hsf_project.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hsf.hsf_project.entity.ActiveLicenseSession;

@Repository
public interface ActiveLicenseSessionRepository extends JpaRepository<ActiveLicenseSession, Long> {
    public Optional<ActiveLicenseSession> findByLicenseKey(String licenseKey);

    public void deleteAllByLastHeartbeatBefore(LocalDateTime cutoff);
}