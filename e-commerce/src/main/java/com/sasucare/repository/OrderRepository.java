package com.sasucare.repository;

import com.sasucare.model.Order;
import com.sasucare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Find orders by customer
    List<Order> findByCustomer(User customer);
    
    // Find orders by status (String status instead of int)
    List<Order> findByOrderStatus(String orderStatus);
    
    // Find orders by customer and status
    List<Order> findByCustomerAndOrderStatus(User customer, String orderStatus);
}
