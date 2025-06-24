package com.bucott.store.mapper;

import com.bucott.store.dto.product.ProductCreateUpdateRequestDTO;
import com.bucott.store.dto.product.ProductCreateUpdateResponseDTO;
import com.bucott.store.dto.product.ProductInfoDTO;
import com.bucott.store.model.product.Product;
import com.bucott.store.model.product.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "categories", ignore = true) // Categories will be handled separately
    Product toEntity(ProductCreateUpdateRequestDTO dto);

    ProductCreateUpdateResponseDTO toCreateUpdateResponseDTO(Product product);

    @Mapping(target = "categoryNames", source = "categories", qualifiedByName = "categoriesToNames")
    ProductInfoDTO toInfoDTO(Product product);

    @Named("categoriesToNames")
    default Set<String> categoriesToNames(Set<ProductCategory> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(ProductCategory::getCategoryName)
                .collect(Collectors.toSet());
    }
}