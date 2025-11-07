package com.hsf.hsf_project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hsf.hsf_project.entity.Product;
import com.hsf.hsf_project.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // add
    public Product addProduct(Product product) {
        validateProduct(product);
        return productRepository.save(product);
    }

    // update
    public Product updateProduct(Long id, Product updatedProduct) {
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        validateProduct(updatedProduct);

        Product product = productRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Product not found"));
        product.setProductName(updatedProduct.getProductName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setQuantity(updatedProduct.getQuantity());
        product.setImageUrl(updatedProduct.getImageUrl());
        return productRepository.save(product);
    }

    // delete
    public void deleteProduct(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    // validation
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        String productName = product.getProductName();
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
        if (product.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
