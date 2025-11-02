package com.hsf.hsf_project.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hsf.hsf_project.entity.Role;
import com.hsf.hsf_project.entity.Users;
import com.hsf.hsf_project.repository.RoleRepository;
import com.hsf.hsf_project.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RoleRepository roleRepository,
                               UserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("🚀 Initializing base data...");

            // --- Tạo Role nếu chưa có ---
            Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN");
            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setRoleName("ROLE_ADMIN");
                roleRepository.save(adminRole);
                System.out.println("✅ Created role: ROLE_ADMIN");
            }

            Role customerRole = roleRepository.findByRoleName("ROLE_CUSTOMER");
            if (customerRole == null) {
                customerRole = new Role();
                customerRole.setRoleName("ROLE_CUSTOMER");
                roleRepository.save(customerRole);
                System.out.println("✅ Created role: ROLE_CUSTOMER");
            }

            // --- Tạo tài khoản admin mặc định ---
            if (userRepository.findByUsername("admin") == null) {
                Users admin = new Users();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setEmail("admin@example.com");
                admin.setRole(adminRole);
                userRepository.save(admin);
                System.out.println("👑 Created admin account: admin / 123456");
            } else {
                System.out.println("ℹ Admin account already exists.");
            }

            // --- Tạo tài khoản customer mẫu ---
            if (userRepository.findByUsername("customer1") == null) {
                Users customer = new Users();
                customer.setUsername("customer1");
                customer.setPassword(passwordEncoder.encode("123456"));
                customer.setEmail("customer1@example.com");
                customer.setRole(customerRole);
                userRepository.save(customer);
                System.out.println("🧍 Created customer account: customer1 / 123456");
            } else {
                System.out.println("ℹ Customer account already exists.");
            }

            System.out.println("✅ Data initialization completed!");
        };
    }
}
