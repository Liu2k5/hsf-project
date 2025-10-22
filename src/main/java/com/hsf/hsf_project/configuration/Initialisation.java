package com.hsf.hsf_project.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hsf.hsf_project.entity.License;
import com.hsf.hsf_project.entity.Product;
import com.hsf.hsf_project.entity.Role;
import com.hsf.hsf_project.entity.Users;
import com.hsf.hsf_project.repository.LicenseRepository;
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

    @Override
    public void run(String... args) throws Exception {
        // Create roles
        // if (roleRepository.findByRoleId(1) == null && roleRepository.findByRoleId(2) == null) {
        if (roleRepository.findAll().isEmpty()) {
            initializeRolesAndUsers();
        }
        if (productRepository.findAll().isEmpty()) {
            initializeProducts();
        }
        if (licenseRepository.findAll().isEmpty()) {
            initializeLicenses();
        }

    }

    private void initializeLicenses() {
        // Create licenses
        if (licenseRepository.count() == 0) {
            License license1 = new License();
            license1.setLicenseKey("LICENSE-001");
            license1.setEnabled(true);
            licenseRepository.save(license1);

            License license2 = new License();
            license2.setLicenseKey("LICENSE-002");
            license2.setEnabled(true);
            licenseRepository.save(license2);
        }
    }

    private void initializeProducts() {
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

    private void initializeRolesAndUsers() {
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
        user1.setRole(roleRepository.findByRoleName("admin"));
        userRepository.save(user1);

        Users user2 = new Users();
        user2.setUsername("customer");
        user2.setPassword(passwordEncoder.encode("customer"));
        user2.setRole(roleRepository.findByRoleName("customer"));
        userRepository.save(user2);
    }
}
