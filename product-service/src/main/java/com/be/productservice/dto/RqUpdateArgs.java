package com.be.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RqUpdateArgs {
    private String id;
    private String name;
    private int price;
    private int quantity;
}
