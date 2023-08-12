package com.be.inventoryservice.mappers;

import com.be.inventoryservice.dto.InventoryDTO;
import com.be.inventoryservice.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IInventoryMapper {
    @Mapping(source = "id", target = "id")
    InventoryDTO InventoryToDTO(Inventory inventory);
}
