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

    @GetMapping("/pay-os-checkout")
    public String payOsCheckout(@RequestParam Long orderId) {
        Orders order = orderService.getOrderById(orderId);
        try {
            CreatePaymentLinkRequest paymentData =
                    CreatePaymentLinkRequest.builder()
                            .orderCode(order.getOrderId())
                            // .expiredAt(order.getPaymentExpiredAt().atZone(ZoneId.systemDefault()).toEpochSecond())
                            .description("FS-" + order.getOrderId())
                            .returnUrl("http://localhost:8080/customer/order-success")
                            // .cancelUrl("http://localhost:8080/customer/order-cancel")
                            .build();
            String checkoutUrl = payOS.paymentRequests().create(paymentData).getCheckoutUrl();
            order.setPaymentLink(checkoutUrl);
            orderService.saveOrder(order);
            return "redirect:" + checkoutUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/customer/shopping-cart";
        }
    }

    @GetMapping(value = "/order-success")
    public String orderSuccess() {
        return "pages/customer/order/success";
    }
}
