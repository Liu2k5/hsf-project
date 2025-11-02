package com.hsf.hsf_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hsf.hsf_project.entity.Orders;
import com.hsf.hsf_project.entity.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByStatus(OrderStatus status);
}
