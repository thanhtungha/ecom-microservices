package com.be.inventoryservice.controller;

import com.be.inventoryservice.AbstractContainerBaseTest;
import com.be.inventoryservice.dto.InventoryDTO;
import com.be.inventoryservice.dto.RqChangeQuantityArgs;
import com.be.inventoryservice.dto.RqProductArgs;
import com.be.inventoryservice.model.Inventory;
import com.be.inventoryservice.model.InventoryItem;
import com.be.inventoryservice.repository.IInventoryRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InventoryControllerTest extends AbstractContainerBaseTest {
    @Autowired
    public MockMvc mockMvc;
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
    void createInventory() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        INVENTORY_BASE_API + "/create-inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse().getContentAsString();
        InventoryDTO responseProduct = objectMapper.readValue(responseBody,
                InventoryDTO.class);
        assertEquals(userDTO.getId(), responseProduct.getOwnerId());
        assertEquals(userDTO.getId(), responseProduct.getOwner().getId());

        //check db
        Optional<Inventory> inventoryOptional = repository.findById(
                responseProduct.getId());
        if (inventoryOptional.isPresent()) {
            assertEquals(userDTO.getId(), inventoryOptional.get().getOwnerId());
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(1)
    void addProduct() throws Exception {
        RqProductArgs productArgs = new RqProductArgs(productDTO1.getId());

        String reqString = objectMapper.writeValueAsString(productArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        INVENTORY_BASE_API + "/add-product")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .content(reqString);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse().getContentAsString();
        InventoryDTO responseProduct = objectMapper.readValue(responseBody,
                InventoryDTO.class);
        assertEquals(1, responseProduct.getInventoryItems().size());

        //check db
        Optional<Inventory> inventoryOptional = repository.findById(
                responseProduct.getId());
        if (inventoryOptional.isPresent()) {
            Inventory dbInventory = inventoryOptional.get();
            List<InventoryItem> inventoryItems =
                    dbInventory.getInventoryItems();
            assertEquals(1, inventoryItems.size());
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(3)
    void removeProduct() throws Exception {
        RqProductArgs productArgs = new RqProductArgs(productDTO1.getId());

        String reqString = objectMapper.writeValueAsString(productArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        INVENTORY_BASE_API + "/remove-product")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .content(reqString);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse().getContentAsString();
        InventoryDTO responseProduct = objectMapper.readValue(responseBody,
                InventoryDTO.class);
        assertEquals(0, responseProduct.getInventoryItems().size());

        //check db
        Optional<Inventory> inventoryOptional = repository.findById(
                responseProduct.getId());
        if (inventoryOptional.isPresent()) {
            Inventory dbInventory = inventoryOptional.get();
            List<InventoryItem> inventoryItems =
                    dbInventory.getInventoryItems();
            assertEquals(0, inventoryItems.size());
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(2)
    void changeQuantity() throws Exception {
        RqChangeQuantityArgs changeQuantityArgs = new RqChangeQuantityArgs(
                productDTO1.getId(), 33);

        String reqString = objectMapper.writeValueAsString(changeQuantityArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        INVENTORY_BASE_API + "/change-quantity")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .content(reqString);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse().getContentAsString();
        InventoryDTO responseProduct = objectMapper.readValue(responseBody,
                InventoryDTO.class);
        assertEquals(1, responseProduct.getInventoryItems().size());
        assertEquals(changeQuantityArgs.getQuantity(),
                responseProduct.getInventoryItems().get(0).getQuantity());

        //check db
        Optional<Inventory> inventoryOptional = repository.findById(
                responseProduct.getId());
        if (inventoryOptional.isPresent()) {
            Inventory dbInventory = inventoryOptional.get();
            List<InventoryItem> inventoryItems =
                    dbInventory.getInventoryItems();
            assertEquals(1, inventoryItems.size());
            assertEquals(changeQuantityArgs.getQuantity(),
                    inventoryItems.get(0).getQuantity());
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(2)
    void getInventory() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                        INVENTORY_BASE_API + "/get-inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse().getContentAsString();
        InventoryDTO responseProduct = objectMapper.readValue(responseBody,
                InventoryDTO.class);
        assertEquals(1, responseProduct.getInventoryItems().size());

        //check db
        Optional<Inventory> inventoryOptional = repository.findById(
                responseProduct.getId());
        if (inventoryOptional.isPresent()) {
            Inventory dbInventory = inventoryOptional.get();
            List<InventoryItem> inventoryItems = dbInventory.getInventoryItems();
            assertEquals(1, inventoryItems.size());
            return;
        }
        fail("test case failed!");
    }
}