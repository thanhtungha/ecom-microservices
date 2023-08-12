package com.be.productservice.service;

import com.be.productservice.dto.ProductDTO;
import com.be.productservice.dto.RqAddReviewArgs;
import com.be.productservice.dto.RqRegisterArgs;
import com.be.productservice.dto.RqUpdateArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductServiceImpl implements IProductService {
    @Override
    public ProductDTO register(String authorizationHeader, RqRegisterArgs registerArgs) {
        return null;
    }

    @Override
    public ProductDTO update(RqUpdateArgs updateArgs) {
        return null;
    }

    @Override
    public boolean remove(String productId) {
        return false;
    }

    @Override
    public ProductDTO getProduct(String productId) {
        return null;
    }

    @Override
    public ProductDTO addReview(String authorizationHeader, RqAddReviewArgs addReviewArgs) {
        return null;
    }
}
