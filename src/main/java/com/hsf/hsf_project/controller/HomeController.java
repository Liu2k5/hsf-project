package com.hsf.hsf_project.controller;

import com.hsf.hsf_project.entity.Product;
import com.hsf.hsf_project.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    // Trang chủ chung
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Chào mừng đến với trang chủ!");
        return "customer/customerHomePage"; // Giao diện cho khách hoặc chưa đăng nhập
    }

    // Trang admin
    @GetMapping("/admin")
    public String adminHome(Model model, Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : "Guest";
        model.addAttribute("adminName", username);

        if (authentication == null ||
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/access-denied";
        }

        return "admin/homepage";
    }

    // Trang customer
    @GetMapping("/customer")
    public String customerHome(
            Model model,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        if (authentication == null ||
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"))) {
            return "redirect:/access-denied";
        }

        String username = (authentication != null) ? authentication.getName() : "Guest";
        model.addAttribute("customerName", username);

        Page<Product> productPage = productService.searchProducts(keyword, minPrice, maxPrice, page, size);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("pageSize", size);

        // Giữ lại giá trị tìm kiếm/lọc
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "customer/customerHomePage";
    }


    // Trang lỗi
    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("error", "Bạn không có quyền truy cập vào trang này!");
        return "error/accessDenied";
    }
}
