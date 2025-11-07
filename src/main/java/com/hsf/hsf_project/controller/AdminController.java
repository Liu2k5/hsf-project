package com.hsf.hsf_project.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hsf.hsf_project.dto.AddUserRequest;
import com.hsf.hsf_project.dto.LicenseDTO;
import com.hsf.hsf_project.dto.OrderDetailDTO;
import com.hsf.hsf_project.dto.OrderSummaryDTO;
import com.hsf.hsf_project.dto.ResetRequest;
import com.hsf.hsf_project.dto.RevenueByDayDTO;
import com.hsf.hsf_project.dto.RevokeRequest;
import com.hsf.hsf_project.dto.StatsSummaryDTO;
import com.hsf.hsf_project.dto.TopProductDTO;
import com.hsf.hsf_project.dto.UpdateRoleRequest;
import com.hsf.hsf_project.dto.UserDTO;
import com.hsf.hsf_project.entity.Product;
import com.hsf.hsf_project.service.DashboardService;
import com.hsf.hsf_project.service.LicenseService;
import com.hsf.hsf_project.service.LicenseSessionService;
import com.hsf.hsf_project.service.OrderService;
import com.hsf.hsf_project.service.ProductService;
import com.hsf.hsf_project.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;
    private final LicenseService licenseService;
    private final LicenseSessionService licenseSessionService;
    private final DashboardService dashboardService;
    private final ProductService productService;

    @RequestMapping("")
    public String adminHome() {
        return "/admin/homepage";
    }

    // User Management APIs
    @GetMapping("/users")
    @ResponseBody
    public Page<UserDTO> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.getUsers(pageable);
    }

    @PostMapping("/users/role")
    @ResponseBody
    public ResponseEntity<String> updateRole(@RequestBody UpdateRoleRequest request) {
        try {
            userService.updateUserRole(request.userId(), request.newRoleName());
            return ResponseEntity.ok("Role updated successfully");
        } catch (IllegalArgumentException e) {
            // Log the actual error for debugging but return a safe message to user
            log.warn("Failed to update user role for userId {}: {}", request.userId(), e.getMessage());
            return ResponseEntity.badRequest().body("Failed to update user role");
        }
    }

    @PostMapping("/users")
    @ResponseBody
    public ResponseEntity<?> addUser(@RequestBody AddUserRequest request) {
        try {
            userService.addUser(request.username(), request.password(), request.email(), request.roleName());
            return ResponseEntity.ok("User added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/users/{userId}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable Long userId,
                                        @AuthenticationPrincipal UserDetails currentUser) {
        try {
            userService.deleteUser(userId, currentUser.getUsername());
            return ResponseEntity.ok("User deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Order Management APIs
    @GetMapping("/orders")
    @ResponseBody
    public Page<OrderSummaryDTO> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate startDate) {
        Pageable pageable = PageRequest.of(page, size);
        return orderService.getOrders(pageable, status, startDate);
    }

    @GetMapping("/orders/{orderId}")
    @ResponseBody
    public OrderDetailDTO getOrderDetails(@PathVariable Long orderId) {
        return orderService.getOrderDetails(orderId);
    }

    // License Management APIs
    @GetMapping("/licenses")
    @ResponseBody
    public Page<LicenseDTO> getLicenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchKey) {
        Pageable pageable = PageRequest.of(page, size);
        return licenseService.getLicenses(pageable, searchKey);
    }

    @PostMapping("/licenses/revoke")
    @ResponseBody
    public ResponseEntity<String> revokeLicense(@RequestBody RevokeRequest request) {
        try {
            System.out.println("Revoke request received: " + request.licenseKey());
            licenseSessionService.revokeLicense(request.licenseKey(), request.reason());
            return ResponseEntity.ok("License revoked");
        } catch (Exception e) {
            System.err.println("Error revoking license: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to revoke license: " + e.getMessage());
        }
    }

    @PostMapping("/licenses/reset")
    @ResponseBody
    public ResponseEntity<String> resetDevice(@RequestBody ResetRequest request) {
        licenseSessionService.resetDevice(request.licenseKey());
        return ResponseEntity.ok("Device reset for license");
    }

    // Dashboard Statistics APIs
    @GetMapping("/stats/summary")
    @ResponseBody
    public StatsSummaryDTO getStatsSummary() {
        return dashboardService.getStatsSummary();
    }

    @GetMapping("/stats/revenue-chart")
    @ResponseBody
    public List<RevenueByDayDTO> getRevenueChartData() {
        return dashboardService.getRevenueChartData();
    }

    @GetMapping("/stats/top-products")
    @ResponseBody
    public List<TopProductDTO> getTopProducts() {
        return dashboardService.getTopProducts();
    }

    // Product Management APIs
    @GetMapping("/products")
    @ResponseBody
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/products")
    @ResponseBody
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        try {
            Product savedProduct = productService.addProduct(product);
            return ResponseEntity.ok(savedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/products/{id}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}