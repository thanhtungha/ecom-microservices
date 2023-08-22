package com.be.inventoryservice.service;

import com.be.inventoryservice.AbstractContainerBaseTest;
import com.be.inventoryservice.dto.InventoryDTO;
import com.be.inventoryservice.dto.RqChangeQuantityArgs;
import com.be.inventoryservice.dto.RqProductArgs;
import com.be.inventoryservice.model.Inventory;
import com.be.inventoryservice.model.InventoryItem;
import com.be.inventoryservice.repository.IInventoryRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InventoryServiceImplTest extends AbstractContainerBaseTest {
    @Autowired
    public IInventoryService service;
    @Autowired
    public IInventoryRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        if (userDTO == null) {
            createTestUser();
        }
    }

    @Test
    @Order(0)
    void createInventory() {
        InventoryDTO response = service.createInventory(userDTO);
        Optional<Inventory> createdInventory = repository.findById(
                response.getId());
        if (createdInventory.isPresent()) {
            Inventory dbInventory = createdInventory.get();
            assertEquals(response.getId(), dbInventory.getId());
        } else {
            fail("test case failed!");
        }
    }

    @Test
    @Order(1)
    void addProduct() {
        RqProductArgs productArgs = new RqProductArgs(productDTO1.getId());

        InventoryDTO response = service.addProduct(authorizationHeader,
                productArgs);
        Optional<Inventory> createdInventory = repository.findById(
                response.getId());
        if (createdInventory.isPresent()) {
            Inventory dbInventory = createdInventory.get();
            List<InventoryItem> inventoryItems =
                    dbInventory.getInventoryItems();
            //check db
            assertEquals(1, inventoryItems.size());

            //check response
            assertEquals(1, response.getInventoryItems().size());
        } else {
            fail("test case failed!");
        }
    }

    @Test
    @Order(3)
    void removeProduct() {
        RqProductArgs productArgs = new RqProductArgs(productDTO1.getId());

        InventoryDTO response = service.removeProduct(authorizationHeader,
                productArgs);
        Optional<Inventory> createdInventory = repository.findById(
                response.getId());
        if (createdInventory.isPresent()) {
            Inventory dbInventory = createdInventory.get();
            List<InventoryItem> inventoryItems =
                    dbInventory.getInventoryItems();
            //check db
            assertEquals(0, inventoryItems.size());

            //check response
            assertEquals(0, response.getInventoryItems().size());
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(2)
    void changeQuantity() {
        RqChangeQuantityArgs changeQuantityArgs = new RqChangeQuantityArgs(
                productDTO1.getId(), 33);

        InventoryDTO response = service.changeQuantity(authorizationHeader,
                changeQuantityArgs);
        Optional<Inventory> createdInventory = repository.findById(
                response.getId());
        if (createdInventory.isPresent()) {
            Inventory dbInventory = createdInventory.get();
            List<InventoryItem> inventoryItems =
                    dbInventory.getInventoryItems();
            //check db
            assertEquals(changeQuantityArgs.getQuantity(),
                    inventoryItems.get(0).getQuantity());

            //check response
            assertEquals(changeQuantityArgs.getQuantity(),
                    response.getInventoryItems().get(0).getQuantity());
        } else {
            fail("test case failed!");
        }
    }

    @Test
    @Order(2)
    void getInventory() {
        InventoryDTO response = service.getInventory(authorizationHeader);
        Optional<Inventory> createdCart = repository.findById(response.getId());
        if (createdCart.isPresent()) {
            Inventory dbCart = createdCart.get();
            assertEquals(dbCart.getInventoryItems().size(),
                    response.getInventoryItems().size());
        } else {
            fail("test case failed!");
        }
    }
}