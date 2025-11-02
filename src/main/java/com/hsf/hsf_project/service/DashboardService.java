package com.hsf.hsf_project.service;

import org.springframework.stereotype.Service;

import com.hsf.hsf_project.dto.RevenueByDayDTO;
import com.hsf.hsf_project.dto.StatsSummaryDTO;
import com.hsf.hsf_project.dto.TopProductDTO;
import com.hsf.hsf_project.repository.ActiveLicenseSessionRepository;
import com.hsf.hsf_project.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DashboardService {
    private final OrderRepository orderRepository;
    private final ActiveLicenseSessionRepository sessionRepository;

    public StatsSummaryDTO getStatsSummary() {
        // Calculate total revenue from completed orders
        double totalRevenue = orderRepository.findAll().stream()
                .filter(order -> "COMPLETED".equals(order.getStatus()))
                .mapToDouble(order -> order.getProduct() != null ? order.getProduct().getPrice() : 0.0)
                .sum();

        // Count new orders today
        String today = LocalDate.now().toString();
        long newOrdersToday = orderRepository.findAll().stream()
                .filter(order -> order.getPaidDate() != null && order.getPaidDate().startsWith(today))
                .count();

        // Count active sessions
        long activeSessions = sessionRepository.count();

        return new StatsSummaryDTO(totalRevenue, newOrdersToday, activeSessions);
    }

    public List<RevenueByDayDTO> getRevenueChartData() {
        // Get orders and group by date, calculating revenue per day
        return orderRepository.findAll().stream()
                .filter(order -> "COMPLETED".equals(order.getStatus()) && order.getPaidDate() != null)
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
        // Group by product and count sales
        return orderRepository.findAll().stream()
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
