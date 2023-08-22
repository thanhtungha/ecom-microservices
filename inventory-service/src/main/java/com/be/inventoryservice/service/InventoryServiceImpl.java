package com.be.inventoryservice.service;

import com.be.inventoryservice.dto.*;
import com.be.inventoryservice.exception.RestExceptions;
import com.be.inventoryservice.mappers.IInventoryMapper;
import com.be.inventoryservice.model.Inventory;
import com.be.inventoryservice.model.InventoryItem;
import com.be.inventoryservice.repository.IInventoryRepository;
import com.be.inventoryservice.service.webclient.AuthClient;
import com.be.inventoryservice.service.webclient.ProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class InventoryServiceImpl implements IInventoryService {
    private final IInventoryRepository repository;
    private final IInventoryMapper mapper;
    private final AuthClient authClient;
    private final ProductClient productClient;

    @Override
    public InventoryDTO createInventory(UserDTO user) {
        Optional<Inventory> storedModel = repository.findByOwnerId(
                user.getId());
        if (storedModel.isEmpty()) {
            Inventory inventory = new Inventory();
            inventory.setCreateDate(new Date());
            inventory.setUpdateDate(new Date());
            inventory.setOwnerId(user.getId());
            repository.save(inventory);
            storedModel = repository.findByOwnerId(user.getId());
        }
        Inventory inventory = storedModel.get();

        InventoryDTO inventoryDTO = mapper.InventoryToDTO(inventory);
        inventoryDTO.setOwner(user);
        return inventoryDTO;
    }

    @Override
    public InventoryDTO addProduct(String authorizationHeader,
                                   RqProductArgs productArgs) {
        UserDTO user = authClient.verifyToken(authorizationHeader);
        Optional<Inventory> storedModel = repository.findByOwnerId(
                user.getId());
        if (storedModel.isEmpty()) {
            throw new RestExceptions.NotFound("Inventory does not existed!");
        }
        Inventory inventory = storedModel.get();
        boolean isAdded = false;
        for (InventoryItem inventoryItem : inventory.getInventoryItems()) {
            if (inventoryItem.getProductId()
                    .equals(productArgs.getProductId())) {
                isAdded = true;
                break;
            }
        }
        if (!isAdded) {
            InventoryItem inventoryItem = new InventoryItem();
            inventoryItem.setCreateDate(new Date());
            inventoryItem.setUpdateDate(new Date());
            inventoryItem.setProductId(productArgs.getProductId());
            inventory.getInventoryItems().add(inventoryItem);
        }
        repository.save(inventory);

        return generateInventoryDTO(inventory, user, authorizationHeader);
    }

    @Override
    public InventoryDTO removeProduct(String authorizationHeader,
                                      RqProductArgs productArgs) {
        UserDTO user = authClient.verifyToken(authorizationHeader);
        Optional<Inventory> storedModel = repository.findByOwnerId(
                user.getId());
        if (storedModel.isEmpty()) {
            throw new RestExceptions.NotFound("Inventory does not existed!");
        }
        Inventory inventory = storedModel.get();
        InventoryItem storedInventoryItem = null;
        for (InventoryItem inventoryItem : inventory.getInventoryItems()) {
            if (inventoryItem.getProductId()
                    .equals(productArgs.getProductId())) {
                storedInventoryItem = inventoryItem;
                break;
            }
        }
        if (storedInventoryItem != null) {
            inventory.getInventoryItems().remove(storedInventoryItem);
        }
        repository.save(inventory);

        return generateInventoryDTO(inventory, user, authorizationHeader);
    }

    @Override
    public InventoryDTO changeQuantity(String authorizationHeader,
                                       RqChangeQuantityArgs changeQuantityArgs) {
        UserDTO user = authClient.verifyToken(authorizationHeader);
        Optional<Inventory> storedModel = repository.findByOwnerId(
                user.getId());
        if (storedModel.isEmpty()) {
            throw new RestExceptions.NotFound("Inventory does not existed!");
        }
        Inventory inventory = storedModel.get();
        InventoryItem storedInventoryItem = null;
        for (InventoryItem inventoryItem : inventory.getInventoryItems()) {
            if (inventoryItem.getProductId()
                    .equals(changeQuantityArgs.getProductId())) {
                storedInventoryItem = inventoryItem;
                break;
            }
        }
        if (storedInventoryItem != null) {
            inventory.getInventoryItems().remove(storedInventoryItem);
            storedInventoryItem.setQuantity(changeQuantityArgs.getQuantity());
            inventory.getInventoryItems().add(storedInventoryItem);
        }
        repository.save(inventory);

        return generateInventoryDTO(inventory, user, authorizationHeader);
    }

    @Override
    public InventoryDTO getInventory(String authorizationHeader) {
        UserDTO user = authClient.verifyToken(authorizationHeader);
        Optional<Inventory> storedModel = repository.findByOwnerId(
                user.getId());
        if (storedModel.isEmpty()) {
            throw new RestExceptions.NotFound("Inventory does not existed!");
        }

        Inventory inventory = storedModel.get();
        return generateInventoryDTO(inventory, user, authorizationHeader);
    }

    private InventoryDTO generateInventoryDTO(Inventory inventory,
                                              UserDTO userDTO,
                                              String authorizationHeader) {
        List<InventoryItem> inventoryItems = inventory.getInventoryItems();
        InventoryDTO result = mapper.InventoryToDTO(inventory);
        result.setOwner(userDTO);

        //Get Product List
        if (!inventoryItems.isEmpty()) {
            List<String> productIds = inventoryItems.stream()
                    .map(orderItem -> orderItem.getProductId().toString())
                    .toList();
            List<ProductDTO> products = productClient.getListProducts(
                    authorizationHeader, productIds);
            for (InventoryItemDTO inventoryItemDTO :
                    result.getInventoryItems()) {
                UUID productId = inventoryItemDTO.getProductId();
                products.stream()
                        .filter(productDTO -> productDTO.getId()
                                .equals(productId))
                        .findFirst()
                        .ifPresent(inventoryItemDTO::setProduct);
            }
        }
        return result;
    }
}
