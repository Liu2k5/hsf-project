package com.hsf.hsf_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hsf.hsf_project.entity.License;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {

    License findByLicenseKey(String licenseKey);
}