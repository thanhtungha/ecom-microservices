package com.be.cartservice.repository;

import com.be.cartservice.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ICartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByOwnerId(UUID ownerId);
}

