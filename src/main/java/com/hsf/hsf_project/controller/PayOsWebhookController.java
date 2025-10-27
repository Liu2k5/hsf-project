package com.hsf.hsf_project.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hsf.hsf_project.entity.Orders;
import com.hsf.hsf_project.service.EmailService;
import com.hsf.hsf_project.service.OrderService;

import vn.payos.PayOS;
import vn.payos.model.webhooks.WebhookData;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RestController
public class PayOsWebhookController {

    private final PayOS payOS;
    private final OrderService orderService;
    private final EmailService emailService;

    private void orderConfirmed(WebhookData paymentData) {
        Long orderId = paymentData.getOrderCode();
        Orders order = orderService.getOrderById(orderId);
        orderService.doWhenOrderConfirmed(order);
        emailService.sendSimpleEmail(order.getUser().getEmail(),
                "Xác nhận thanh toán cho đơn hàng " + orderId + " thành công",
                "Đơn hàng " + orderId + " đã được thanh toán thành công với số tiền "
                        + paymentData.getAmount() + paymentData.getCurrency() + ".\n" +
                        "Thời gian thanh toán thành công: " + paymentData.getTransactionDateTime() + "\n" +
                        "Cảm ơn bạn đã tin tưởng và sử dụng dịch vụ của chúng tôi!");
    }

    @PostMapping(path = "/webhook")
    public void payosTransferHandler(@RequestBody Object body) {
        WebhookData data = payOS.webhooks().verify(body);
        CompletableFuture.runAsync(() -> orderConfirmed(data));
    }
}