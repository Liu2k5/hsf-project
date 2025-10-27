package com.hsf.hsf_project.service;

import org.springframework.stereotype.Service;

import com.hsf.hsf_project.entity.Orders;
import com.hsf.hsf_project.entity.Product;
import com.hsf.hsf_project.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;

	public Orders getOrderById(Long orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found"));
	}

    public void saveOrder(Orders order) {
        orderRepository.save(order);
    }

    public void doWhenOrderConfirmed(Orders order) {
        reduceStock(order);
    }

	private void reduceStock(Orders order) {
        Product product = order.getProduct();
        int newStock = product.getQuantity() - 1;
        product.setQuantity(newStock);
    }

}
