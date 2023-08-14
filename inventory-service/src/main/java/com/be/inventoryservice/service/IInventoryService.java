package com.be.inventoryservice.service;

import com.be.inventoryservice.dto.InventoryDTO;
import com.be.inventoryservice.dto.RqChangeQuantityArgs;
import com.be.inventoryservice.dto.RqProductArgs;

public interface IInventoryService {
    InventoryDTO createInventory(String authorizationHeader);
    InventoryDTO addProduct(String authorizationHeader, RqProductArgs productArgs);
    InventoryDTO removeProduct(String authorizationHeader, RqProductArgs productArgs);
    InventoryDTO changeQuantity(String authorizationHeader, RqChangeQuantityArgs changeQuantityArgs);
    InventoryDTO getInventory(String authorizationHeader);
}
