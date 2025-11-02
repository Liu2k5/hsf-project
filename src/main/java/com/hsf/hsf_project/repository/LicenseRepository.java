package com.hsf.hsf_project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hsf.hsf_project.entity.License;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {

    Optional<License> findByLicenseKey(String licenseKey);
    
    List<License> findByOrder_User_UserId(Long userId);
    
    List<License> findByOrder_OrderId(Long orderId);
}