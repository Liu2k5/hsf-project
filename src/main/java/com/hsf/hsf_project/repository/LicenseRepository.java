package com.hsf.hsf_project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hsf.hsf_project.entity.License;
import com.hsf.hsf_project.entity.Orders;

public interface LicenseRepository extends JpaRepository<License, Long>, JpaSpecificationExecutor<License> {


    Optional<License> findByLicenseKey(String licenseKey);
}