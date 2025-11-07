package com.hsf.hsf_project.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hsf.hsf_project.entity.Orders;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {

    @Query("SELECT COALESCE(SUM(o.product.price), 0) FROM Orders o WHERE o.status = 'COMPLETED'")
    Double calculateTotalRevenue();

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.paidDate LIKE :datePrefix")
    Long countOrdersByDate(@Param("datePrefix") String datePrefix);

    @Query("SELECT o FROM Orders o WHERE o.status = 'COMPLETED' AND o.paidDate IS NOT NULL")
    List<Orders> findCompletedOrdersWithPaidDate();

    @Query("SELECT o FROM Orders o WHERE o.product IS NOT NULL")
    List<Orders> findAllWithProduct();

    @Query("""
        SELECT o FROM Orders o 
        WHERE o.user.userId = :userId
        AND (:status IS NULL OR :status = '' OR o.status = :status)
        """)
    Page<Orders> searchOrdersByUser(
            @Param("userId") Long userId,
            @Param("status") String status,
            Pageable pageable
    );

}
