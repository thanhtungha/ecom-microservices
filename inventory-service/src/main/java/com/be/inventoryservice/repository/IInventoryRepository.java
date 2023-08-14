package com.be.inventoryservice.repository;

import com.be.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IInventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<Inventory> findByOwnerId(UUID ownerId);
}

