package com.be.productservice.service;

import com.be.productservice.dto.ProductDTO;
import com.be.productservice.dto.RqAddReviewArgs;
import com.be.productservice.dto.RqRegisterArgs;
import com.be.productservice.dto.RqUpdateArgs;

public interface IProductService {
    ProductDTO register(String authorizationHeader, RqRegisterArgs registerArgs);
    ProductDTO update(RqUpdateArgs updateArgs);
    boolean remove(String productId);
    ProductDTO getProduct(String productId);
    ProductDTO addReview(String authorizationHeader, RqAddReviewArgs addReviewArgs);
}
