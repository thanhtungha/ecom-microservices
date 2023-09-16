package com.be.productservice.service;

import com.be.productservice.dto.*;
import com.be.productservice.exception.RestExceptions;
import com.be.productservice.mappers.IProductMapper;
import com.be.productservice.model.Product;
import com.be.productservice.model.Review;
import com.be.productservice.repository.IProductRepository;
import com.be.productservice.service.webclient.AuthClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class ProductServiceImpl implements IProductService {
    private final IProductRepository repository;
    private final IProductMapper mapper;
    private final AuthClient authClient;

    @Override
    public ProductDTO register(String authorizationHeader,
                               RqRegisterArgs registerArgs) {
        UserDTO userDTO = authClient.verifyToken(authorizationHeader);
        Optional<Product> storedModel =
                repository.findByName(registerArgs.getName());
        if (storedModel.isPresent()) {
            throw new RestExceptions.Conflict("Product is existed!");
        }

        Product product = mapper.RegisterArgsToProduct(registerArgs);
        product.setCreateDate(new Date());
        product.setUpdateDate(new Date());
        product.setOwnerId(userDTO.getId());
        repository.save(product);

        ProductDTO result = mapper.ProductToDTO(product);
        result.setOwner(userDTO);
        return result;
    }

    @Override
    public ProductDTO update(String authorizationHeader,
                             RqUpdateArgs updateArgs) {
        UserDTO userDTO = authClient.verifyToken(authorizationHeader);
        Optional<Product> storedModel = repository.findById(UUID.fromString(
                updateArgs.getId()));
        if (storedModel.isEmpty()) {
            throw new RestExceptions.NotFound("Product does not existed!");
        }

        Product product = storedModel.get();
        product.setUpdateDate(new Date());
        product.setName(updateArgs.getName());
        product.setPrice(updateArgs.getPrice());
        product.setQuantity(updateArgs.getQuantity());
        repository.save(product);

        ProductDTO result = mapper.ProductToDTO(product);
        result.setOwner(userDTO);
        return result;
    }

    @Override
    public boolean remove(String authorizationHeader, String productId) {
        authClient.verifyToken(authorizationHeader);
        Optional<Product> storedModel = repository.findById(UUID.fromString(
                productId));
        if (storedModel.isPresent()) {
            repository.delete(storedModel.get());
            return true;
        } else {
            throw new RestExceptions.NotFound("Product does not existed!");
        }
    }

    @Override
    public ProductDTO getProduct(String authorizationHeader, String productId) {
        UserDTO userDTO = authClient.verifyToken(authorizationHeader);
        Optional<Product> storedModel = repository.findById(UUID.fromString(
                productId));
        if (storedModel.isEmpty()) {
            throw new RestExceptions.NotFound("Product does not existed!");
        }

        ProductDTO result = mapper.ProductToDTO(storedModel.get());
        result.setOwner(userDTO);
        return result;
    }

    @Override
    public ProductDTO addReview(String authorizationHeader,
                                RqAddReviewArgs addReviewArgs) {
        UserDTO userDTO = authClient.verifyToken(authorizationHeader);
        Optional<Product> storedModel = repository.findById(UUID.fromString(
                addReviewArgs.getId()));
        if (storedModel.isEmpty()) {
            throw new RestExceptions.NotFound("Product does not existed!");
        }

        int rating = addReviewArgs.getRating();
        String reviewString = addReviewArgs.getReview();

        Product product = storedModel.get();
        product.setUpdateDate(new Date());
        Review review = new Review();
        review.setRate(rating);
        review.setReview(reviewString);
        review.setReviewerId(userDTO.getId());
        product.getReviews()
                .add(review);
        repository.save(product);

        //Get Reviewer List
        List<String> reviewerIds = product.getReviews()
                .stream()
                .map(review1 -> review1.getReviewerId()
                        .toString())
                .toList();
        List<UserDTO> reviewers = authClient.getListUserDTO(authorizationHeader,
                reviewerIds);
        ProductDTO result = mapper.ProductToDTO(storedModel.get());
        for (ReviewDTO rv : result.getReviews()) {
            UUID reviewerId = rv.getReviewerId();
            reviewers.stream()
                    .filter(user -> user.getId()
                            .equals(reviewerId))
                    .findFirst()
                    .ifPresent(rv::setReviewer);
        }
        result.setOwner(userDTO);
        return result;
    }

    @Override
    public ListProducts getListProduct(String authorizationHeader,
                                       List<String> ids) {
        authClient.verifyToken(authorizationHeader);

        List<Product> storedModel;
        if (!ids.isEmpty()) {
            List<UUID> uuidList = ids.stream()
                    .map(UUID::fromString)
                    .toList();
            storedModel = repository.findAllById(uuidList);
        } else {
            storedModel = repository.findAll();
        }

        if (!storedModel.isEmpty()) {
            List<ProductDTO> productDTOList = storedModel.stream()
                    .map(mapper::ProductToDTO)
                    .toList();

            return new ListProducts(productDTOList);
        }
        return null;
    }
}
