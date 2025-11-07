package com.hsf.hsf_project.service;

import com.hsf.hsf_project.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hsf.hsf_project.dto.LicenseDTO;
import com.hsf.hsf_project.entity.License;
import com.hsf.hsf_project.repository.LicenseRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private static final String PREFIX = "LICENSE-";
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

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

    public String generateKey() {
        String key;
        // Sinh key cho đến khi không trùng
        do {
            StringBuilder randomPart = new StringBuilder(3); // 3 ký tự ngẫu nhiên
            for (int i = 0; i < 3; i++) {
                int index = RANDOM.nextInt(CHARSET.length());
                randomPart.append(CHARSET.charAt(index));
            }
            key = PREFIX + randomPart.toString();
        } while (!licenseRepository.findByLicenseKey(key).isEmpty());
        return key;
    }

    public void changeLicenseKey(Long licenseId) {
        License license = licenseRepository.findById(licenseId).orElse(null);
        if(license!=null){
            license.setLicenseKey(generateKey());
            licenseRepository.save(license);
        }
    }

    public License createLicense(Orders order) {
        License license = new License();
        license.setOrder(order);
        license.setEnabled(true);
        license.setLicenseKey(generateKey());
        return licenseRepository.save(license);
    }

    public License findLicenseById(Long licenseId) {
        return licenseRepository.findById(licenseId).orElse(null);
    }
}
