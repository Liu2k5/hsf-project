package com.hsf.hsf_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hsf.hsf_project.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>{

    Users findByUsername(String string);
    
    List<Users> findByRole_RoleId(Integer roleId);
}
