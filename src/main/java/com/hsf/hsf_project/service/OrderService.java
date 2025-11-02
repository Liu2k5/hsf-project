package com.hsf.hsf_project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hsf.hsf_project.dto.LicenseDTO;
import com.hsf.hsf_project.dto.OrderDetailDTO;
import com.hsf.hsf_project.dto.OrderSummaryDTO;
import com.hsf.hsf_project.entity.License;
import com.hsf.hsf_project.entity.Orders;
import com.hsf.hsf_project.repository.LicenseRepository;
import com.hsf.hsf_project.repository.OrderRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final LicenseRepository licenseRepository;

    public Page<OrderSummaryDTO> getOrders(Pageable pageable, String status, LocalDate startDate) {
        Specification<Orders> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("paidDate"), startDate.toString()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return orderRepository.findAll(spec, pageable).map(order -> {
            OrderSummaryDTO dto = new OrderSummaryDTO();
            dto.setOrderId(order.getOrderId());
            dto.setUsername(order.getUser() != null ? order.getUser().getUsername() : null);
            dto.setProductName(order.getProduct() != null ? order.getProduct().getProductName() : null);
            dto.setPaidDate(order.getPaidDate());
            dto.setStatus(order.getStatus());
            return dto;
        });
    }

    public OrderDetailDTO getOrderDetails(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUsername(order.getUser() != null ? order.getUser().getUsername() : null);
        dto.setProductName(order.getProduct() != null ? order.getProduct().getProductName() : null);
        dto.setPaidDate(order.getPaidDate());
        dto.setStatus(order.getStatus());

        // Find license for this order
        licenseRepository.findAll().stream()
                .filter(license -> license.getOrder() != null && license.getOrder().getOrderId().equals(orderId))
                .findFirst()
                .ifPresent(license -> {
                    LicenseDTO licenseDTO = new LicenseDTO();
                    licenseDTO.setLicenseId(license.getLicenseId());
                    licenseDTO.setLicenseKey(license.getLicenseKey());
                    licenseDTO.setUsername(order.getUser() != null ? order.getUser().getUsername() : null);
                    licenseDTO.setProductName(order.getProduct() != null ? order.getProduct().getProductName() : null);
                    licenseDTO.setEnabled(license.isEnabled());
                    dto.setLicense(licenseDTO);
                });

        return dto;
    }
}
