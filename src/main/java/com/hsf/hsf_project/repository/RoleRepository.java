package com.hsf.hsf_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hsf.hsf_project.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>{

    Role findByRoleId(int i);

    Role findByRoleName(String string);

}