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
public class License {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long licenseId;

    @OneToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "order-id")
    private Orders order;

    @Column(unique = true, nullable = false)
    private String licenseKey;

    private boolean enabled;
}
