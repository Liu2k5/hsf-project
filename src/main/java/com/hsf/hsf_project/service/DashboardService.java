package com.hsf.hsf_project.service;

import org.springframework.stereotype.Service;

import com.hsf.hsf_project.dto.RevenueByDayDTO;
import com.hsf.hsf_project.dto.StatsSummaryDTO;
import com.hsf.hsf_project.dto.TopProductDTO;
import com.hsf.hsf_project.repository.ActiveLicenseSessionRepository;
import com.hsf.hsf_project.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DashboardService {
    private final OrderRepository orderRepository;
    private final ActiveLicenseSessionRepository sessionRepository;

    public StatsSummaryDTO getStatsSummary() {
        // Calculate total revenue from completed orders using optimized query
        Double totalRevenue = orderRepository.calculateTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = 0.0;
        }

        // Count new orders today using optimized query
        String today = LocalDate.now().toString();
        Long newOrdersToday = orderRepository.countOrdersByDate(today + "%");

        // Count active sessions
        long activeSessions = sessionRepository.count();

        return new StatsSummaryDTO(totalRevenue, newOrdersToday, activeSessions);
    }

    public List<RevenueByDayDTO> getRevenueChartData() {
        // Get completed orders with paid date using optimized query
        return orderRepository.findCompletedOrdersWithPaidDate().stream()
                .filter(order -> order.getPaidDate() != null && order.getPaidDate().length() >= 10)
                .collect(Collectors.groupingBy(
                        order -> {
                            try {
                                // Parse date string to LocalDate
                                return LocalDate.parse(order.getPaidDate().substring(0, 10));
                            } catch (Exception e) {
                                return LocalDate.now();
                            }
                        },
                        Collectors.summingDouble(order -> order.getProduct() != null ? order.getProduct().getPrice() : 0.0)
                ))
                .entrySet().stream()
                .map(entry -> new RevenueByDayDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());
    }

    public List<TopProductDTO> getTopProducts() {
        // Get orders with products using optimized query
        return orderRepository.findAllWithProduct().stream()
                .filter(order -> order.getProduct() != null)
                .collect(Collectors.groupingBy(
                        order -> order.getProduct().getProductName(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new TopProductDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getSalesCount(), a.getSalesCount()))
                .limit(5)
                .collect(Collectors.toList());
    }
}
