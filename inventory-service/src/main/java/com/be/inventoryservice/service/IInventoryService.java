package com.be.inventoryservice.service;

import com.be.inventoryservice.dto.InventoryDTO;
import com.be.inventoryservice.dto.RqChangeQuantityArgs;
import com.be.inventoryservice.dto.RqProductArgs;
import com.be.inventoryservice.dto.UserDTO;

public interface IInventoryService {
    InventoryDTO createInventory(UserDTO userDTO);
    InventoryDTO addProduct(String authorizationHeader, RqProductArgs productArgs);
    InventoryDTO removeProduct(String authorizationHeader, RqProductArgs productArgs);
    InventoryDTO changeQuantity(String authorizationHeader, RqChangeQuantityArgs changeQuantityArgs);
    InventoryDTO getInventory(String authorizationHeader);
}
