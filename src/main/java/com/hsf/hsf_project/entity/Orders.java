package com.hsf.hsf_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user-id")
    private Users user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product-id")
    private Product product;
    
    private String paidDate;

    private String paymentLink;

    private String status = "COMPLETED";

    @OneToOne(mappedBy = "order", fetch = FetchType.EAGER)
    private License license;
}
