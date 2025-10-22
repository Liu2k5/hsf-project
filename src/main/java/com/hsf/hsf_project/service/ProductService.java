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
}
