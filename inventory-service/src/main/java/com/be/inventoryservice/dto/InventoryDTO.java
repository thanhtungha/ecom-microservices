package com.be.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryDTO {
    private UUID id;
    private Date createDate;
    private Date updateDate;
    private UUID ownerId;
    private UserDTO owner;
    private List<InventoryItemDTO> inventoryItems;
}
