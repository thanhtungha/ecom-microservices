package com.be.productservice.service;

import com.be.productservice.dto.*;

import java.util.List;

public interface IProductService {
    ProductDTO register(String authorizationHeader, RqRegisterArgs registerArgs);
    ProductDTO update(String authorizationHeader, RqUpdateArgs updateArgs);
    boolean remove(String authorizationHeader, String productId);
    ProductDTO getProduct(String authorizationHeader, String productId);
    ProductDTO addReview(String authorizationHeader, RqAddReviewArgs addReviewArgs);
    ListProducts getListProduct(String authorizationHeader, List<String> ids);
}
