package com.hsf.hsf_project.service;

import com.hsf.hsf_project.entity.Product;
import com.hsf.hsf_project.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final ProductRepository productRepository;

    public Orders getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void saveOrder(Orders order) {
        orderRepository.save(order);
    }

    public void doWhenOrderConfirmed(Orders order) {
        reduceStock(order);
    }

    private void reduceStock(Orders order) {
        Product product = order.getProduct();
        int newStock = product.getQuantity() - 1;
        product.setQuantity(newStock);
        productRepository.save(product);
    }

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

        // Find license for this order using repository method
        licenseRepository.findByOrder(order).ifPresent(license -> {
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

    public List<Orders> getOrdersByCustomerId(Long customerId) {
        List<Orders> orders = new ArrayList<>();
        for (Orders order : orderRepository.findAll()) {
            if(order.getUser().getUserId().equals(customerId)){
                orders.add(order);
            }
        }
        return orders;
    }

    public Page<Orders> searchOrdersByCustomer(Long userId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderId").descending());
        return orderRepository.searchOrdersByUser(userId, status, pageable);
    }
}