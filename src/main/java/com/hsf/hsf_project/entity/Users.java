package com.hsf.hsf_project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long userId;

    private String username;

    private String password;

    private String email;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="roleId")
    private Role role;
}
