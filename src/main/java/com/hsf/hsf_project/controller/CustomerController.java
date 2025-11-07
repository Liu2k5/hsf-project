package com.hsf.hsf_project.controller;

import com.hsf.hsf_project.entity.License;
import com.hsf.hsf_project.entity.Product;
import com.hsf.hsf_project.entity.Users;
import com.hsf.hsf_project.service.LicenseService;
import com.hsf.hsf_project.service.OrderService;
import com.hsf.hsf_project.service.ProductService;
import com.hsf.hsf_project.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hsf.hsf_project.entity.Orders;

import lombok.RequiredArgsConstructor;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RequestMapping("/customer")
@Controller
public class CustomerController {
    private final OrderService orderService;
    private final PayOS payOS;
    private final UserService userService;
    private final ProductService productService;
    private final LicenseService licenseService;

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

    @GetMapping("/product/{productId}")
    public String productDetail(@PathVariable Long productId, Model model) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return "redirect:/";
        }
        model.addAttribute("product", product);
        return "customer/productDetail";
    }
    @GetMapping("/oders")
    public String MyOrder(){

        return "myLicense";
    }
    @GetMapping("/buy/{productId}")
    public String buyProduct(@PathVariable Long productId, Authentication authentication){
        if (authentication == null ||
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"))) {
            return "redirect:/access-denied";
        }
        String username = (authentication != null) ? authentication.getName() : "Guest";
        Users user = userService.findByUsername(username);
        Product product = productService.getProductById(productId);
        if(user != null&&product != null&&product.getQuantity()>0){
            Orders order = new Orders();
            order.setProduct(product);
            order.setUser(user);
            order.setPaidDate(LocalDateTime.now().toString());
            orderService.saveOrder(order);
            licenseService.createLicense(order);
            orderService.doWhenOrderConfirmed(order);
        }
        return "redirect:/customer/product/"+productId;
    }

    @GetMapping("/myLicense")
    public String myLicense(
            Model model,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String status
    ) {
        if (authentication == null ||
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"))) {
            return "redirect:/access-denied";
        }

        String username = authentication.getName();
        Users user = userService.findByUsername(username);

        Page<Orders> orderPage = orderService.searchOrdersByCustomer(user.getUserId(), status, page, size);

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("pageSize", size);

        // giữ lại filter
        model.addAttribute("status", status);

        // các trạng thái có thể lọc
        List<String> statuses = List.of("PENDING", "COMPLETED", "FAILED");
        model.addAttribute("statuses", statuses);

        return "customer/myLicense";
    }
    @GetMapping("/changeLicense/{licenseId}")
    public String changeLicense(@PathVariable Long licenseId, Authentication authentication){
        if (authentication == null ||
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"))) {
            return "redirect:/access-denied";
        }
        String username = (authentication != null) ? authentication.getName() : "Guest";
        Users user = userService.findByUsername(username);
        List<Orders> orders = orderService.getOrdersByCustomerId(user.getUserId());
        List<License> licenses = orders.stream().map(Orders::getLicense)
                .filter(Objects::nonNull)
                .toList();
        for(License l : licenses){
            System.out.println(l.getLicenseId());
        }
        License license = licenseService.findLicenseById(licenseId);
        if(license != null&&orders!=null&&licenses.contains(license)){
            licenseService.changeLicenseKey(licenseId);
        }
        return "redirect:/customer/myLicense";
    }
}
