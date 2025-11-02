package com.hsf.hsf_project.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hsf.hsf_project.entity.Orders;
import com.hsf.hsf_project.entity.enums.OrderStatus;
import com.hsf.hsf_project.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final OrderRepository orderRepository;
    
    @RequestMapping("")
    public String adminHome(Model model) {
        // Get all completed orders from database
        List<Orders> completedOrders = orderRepository.findByStatus(OrderStatus.COMPLETED);
        
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        
        // Calculate revenue for today
        double dailyRevenue = completedOrders.stream()
                .filter(order -> {
                    try {
                        LocalDate orderDate = LocalDate.parse(order.getPaidDate(), DATE_FORMATTER);
                        return orderDate.equals(today);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .mapToDouble(order -> order.getProduct().getPrice())
                .sum();
        
        // Calculate revenue for current month
        double monthlyRevenue = completedOrders.stream()
                .filter(order -> {
                    try {
                        LocalDate orderDate = LocalDate.parse(order.getPaidDate(), DATE_FORMATTER);
                        return orderDate.getYear() == currentYear && orderDate.getMonthValue() == currentMonth;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .mapToDouble(order -> order.getProduct().getPrice())
                .sum();
        
        // Calculate revenue for current year
        double yearlyRevenue = completedOrders.stream()
                .filter(order -> {
                    try {
                        LocalDate orderDate = LocalDate.parse(order.getPaidDate(), DATE_FORMATTER);
                        return orderDate.getYear() == currentYear;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .mapToDouble(order -> order.getProduct().getPrice())
                .sum();
        
        model.addAttribute("dailyRevenue", dailyRevenue);
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        model.addAttribute("yearlyRevenue", yearlyRevenue);
        
        return "/admin/homepage";
    }
}
