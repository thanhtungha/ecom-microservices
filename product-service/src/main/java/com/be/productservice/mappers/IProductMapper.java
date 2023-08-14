package com.be.productservice.mappers;

import com.be.productservice.dto.ProductDTO;
import com.be.productservice.dto.RqRegisterArgs;
import com.be.productservice.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IProductMapper {
    @Mapping(source = "name", target = "name")
    Product RegisterArgsToProduct(RqRegisterArgs registerArgs);

    @Mapping(source = "id", target = "id")
    ProductDTO ProductToDTO(Product product);
}
