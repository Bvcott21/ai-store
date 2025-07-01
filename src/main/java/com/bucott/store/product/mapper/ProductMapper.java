package com.bucott.store.product.mapper;

import com.bucott.store.product.dto.ProductCreateUpdateRequestDTO;
import com.bucott.store.product.dto.ProductCreateUpdateResponseDTO;
import com.bucott.store.product.dto.ProductInfoDTO;
import com.bucott.store.product.model.Product;
import com.bucott.store.product.model.ProductCategory;
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

    @Mapping(target = "categoryIds", source = "categories", qualifiedByName = "categoriesToIds")
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

    @Named("categoriesToIds")
    default Long[] categoriesToIds(Set<ProductCategory> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(ProductCategory::getProductCategoryId)
                .toArray(Long[]::new);
    }
}