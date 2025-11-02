package com.hsf.hsf_project.controller;

import java.time.ZoneId;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hsf.hsf_project.entity.Orders;
import com.hsf.hsf_project.service.OrderService;

import lombok.RequiredArgsConstructor;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;

@RequiredArgsConstructor
@RequestMapping("/customer")
@Controller
public class CustomerController {
    private final OrderService orderService;
    private final PayOS payOS;

    @GetMapping(value = "/order-success")
    public String orderSuccess() {
        return "pages/customer/order/success";
    }
}
