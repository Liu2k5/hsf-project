package com.hsf.hsf_project.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hsf.hsf_project.entity.Orders;
import com.hsf.hsf_project.entity.enums.OrderStatus;
import com.hsf.hsf_project.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {
    
    private final OrderRepository orderRepository;
    
    @GetMapping("")
    public String viewOrders(@RequestParam(required = false) OrderStatus status, Model model) {
        List<Orders> orders;
        
        if (status != null) {
            orders = orderRepository.findByStatus(status);
        } else {
            orders = orderRepository.findAll();
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("orderStatuses", OrderStatus.values());
        
        return "/admin/orders/list";
    }
    
    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model) {
        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));
        model.addAttribute("order", order);
        return "/admin/orders/detail";
    }
    
    @PostMapping("/{id}/update-status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));
        order.setStatus(status);
        orderRepository.save(order);
        return "redirect:/admin/orders/" + id;
    }
}
