package com.hsf.hsf_project.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hsf.hsf_project.entity.ActiveLicenseSession;
import com.hsf.hsf_project.entity.License;
import com.hsf.hsf_project.entity.Orders;
import com.hsf.hsf_project.entity.Product;
import com.hsf.hsf_project.entity.Role;
import com.hsf.hsf_project.entity.Users;
import com.hsf.hsf_project.repository.ActiveLicenseSessionRepository;
import com.hsf.hsf_project.repository.LicenseRepository;
import com.hsf.hsf_project.repository.OrderRepository;
import com.hsf.hsf_project.repository.ProductRepository;
import com.hsf.hsf_project.repository.RoleRepository;
import com.hsf.hsf_project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class Initialisation implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final LicenseRepository licenseRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;
    private final ActiveLicenseSessionRepository activeLicenseSessionRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findAll().isEmpty()) {
            initialiseRolesAndUsers();
        }
        if (productRepository.findAll().isEmpty()) {
            initialiseProducts();
        }
        if (orderRepository.findAll().isEmpty()) {
            initialiseOrders();
        }
        if (licenseRepository.findAll().isEmpty()) {
            initialiseLicenses();
        }
        if (activeLicenseSessionRepository.findAll().isEmpty()) {
            initialiseActiveLicenseSessions();
        }
    }

    private void initialiseActiveLicenseSessions() {
        ActiveLicenseSession session1 = new ActiveLicenseSession();
        License license1 = licenseRepository.findByLicenseKey("LICENSE-001")
                .orElseThrow(() -> new RuntimeException("License not found"));
        session1.setLicense(license1);
        session1.setLicenseKey(license1.getLicenseKey());
        session1.setDeviceId("DEVICE-001");
        session1.setStartedAt(java.time.LocalDateTime.now());
        session1.setLastHeartbeat(java.time.LocalDateTime.now());
        activeLicenseSessionRepository.save(session1);
    }

    private void initialiseOrders() {
        Orders order1 = new Orders();
        order1.setProduct(productRepository.findById(1L).orElse(null));
        order1.setUser(userRepository.findById(1L).orElse(null));
        orderRepository.save(order1);

        Orders order2 = new Orders();
        order2.setProduct(productRepository.findById(2L).orElse(null));
        order2.setUser(userRepository.findById(2L).orElse(null));
        orderRepository.save(order2);
    }

    private void initialiseLicenses() {
        License license1 = new License();
        license1.setOrder(orderRepository.findById(1L).orElseThrow(() -> new RuntimeException("Order not found")));
        license1.setLicenseKey("LICENSE-001");
        license1.setEnabled(true);
        licenseRepository.save(license1);

        License license2 = new License();
        license2.setOrder(orderRepository.findById(2L).orElseThrow(() -> new RuntimeException("Order not found")));
        license2.setLicenseKey("LICENSE-002");
        license2.setEnabled(true);
        licenseRepository.save(license2);
    }

    private void initialiseProducts() {
        Product product1 = new Product();
        product1.setProductName("Product 1");
        product1.setDescription("Description for Product 1");
        product1.setPrice(10.0);
        product1.setQuantity(100);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setProductName("Product 2");
        product2.setDescription("Description for Product 2");
        product2.setPrice(20.0);
        product2.setQuantity(200);
        productRepository.save(product2);
    }

    private void initialiseRolesAndUsers() {
        Role adminRole = new Role();
        adminRole.setRoleName("admin");

        Role userRole = new Role();
        userRole.setRoleName("customer");
        roleRepository.save(adminRole);
        roleRepository.save(userRole);
        
        // Create users
        Users user1 = new Users();
        user1.setUsername("admin");
        user1.setPassword(passwordEncoder.encode("admin"));
        user1.setEmail("manager@shop.com");
        user1.setRole(roleRepository.findByRoleName("admin"));
        userRepository.save(user1);

        Users user2 = new Users();
        user2.setUsername("customer");
        user2.setPassword(passwordEncoder.encode("customer"));
        user2.setEmail("customer@shop.com");
        user2.setRole(roleRepository.findByRoleName("customer"));
        userRepository.save(user2);
    }
}
