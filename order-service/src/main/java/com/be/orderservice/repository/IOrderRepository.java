package com.be.orderservice.repository;

import com.be.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IOrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByIdAndOwnerId(UUID uuid, UUID ownerId);
}

