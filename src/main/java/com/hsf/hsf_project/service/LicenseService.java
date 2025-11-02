package com.hsf.hsf_project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hsf.hsf_project.dto.LicenseDTO;
import com.hsf.hsf_project.entity.License;
import com.hsf.hsf_project.repository.LicenseRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LicenseService {
    private final LicenseRepository licenseRepository;

    public Page<LicenseDTO> getLicenses(Pageable pageable, String searchKey) {
        Specification<License> spec = (root, query, criteriaBuilder) -> {
            if (searchKey == null || searchKey.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            
            // Search by license key
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.get("licenseKey")), 
                "%" + searchKey.toLowerCase() + "%"
            ));
            
            // Search by username
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.join("order").join("user").get("username")),
                "%" + searchKey.toLowerCase() + "%"
            ));
            
            // Search by product name
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(root.join("order").join("product").get("productName")),
                "%" + searchKey.toLowerCase() + "%"
            ));
            
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };

        return licenseRepository.findAll(spec, pageable).map(license -> {
            LicenseDTO dto = new LicenseDTO();
            dto.setLicenseId(license.getLicenseId());
            dto.setLicenseKey(license.getLicenseKey());
            if (license.getOrder() != null) {
                dto.setUsername(license.getOrder().getUser() != null ? 
                    license.getOrder().getUser().getUsername() : null);
                dto.setProductName(license.getOrder().getProduct() != null ? 
                    license.getOrder().getProduct().getProductName() : null);
            }
            dto.setEnabled(license.isEnabled());
            return dto;
        });
    }
}
