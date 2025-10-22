package com.hsf.hsf_project.entity;

import org.hibernate.annotations.Nationalized;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Nationalized
    private String productName;

    private String imageUrl;

    @Nationalized
    private String description;

    private double price;
    
    private int quantity;
}
