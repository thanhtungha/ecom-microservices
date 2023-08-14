package com.be.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDTO {
    private UUID id;
    private Date createDate;
    private Date updateDate;
    private UUID productId;
    private ProductDTO product;
    private int quantity;
}
