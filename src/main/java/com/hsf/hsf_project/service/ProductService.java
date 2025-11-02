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
        return productRepository.save(product);
    }

    // update
    public Product updateProduct(Long id, Product updatedProduct) {
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
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }
}