package com.be.orderservice.dto;

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
public class ReviewDTO {
    private UUID id;
    private Date createDate;
    private Date updateDate;
    private int rate;
    private String review;
    private UUID reviewerId;
    private UserDTO reviewer;
}
