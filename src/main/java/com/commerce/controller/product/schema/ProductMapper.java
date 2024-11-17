package com.commerce.controller.product.schema;

import com.commerce.model.Image;
import com.commerce.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductMapper {

    public ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "images", target = "images")
    ProductMapper.GetProduct toGetProduct(Product product);

    GetImage toImageDTO(Image image);

    default Page<GetProduct> toGetProductPage(Page<Product> products) {
        return products.map(this::toGetProduct);
    }

    record GetProduct(
            Long id,
            String name,
            String categoryName,
            List<GetImage> images,
            String shortDescription,
            String longDescription,
            Integer stock,
            String productId,
            BigDecimal price,
            boolean active,
            String seoDescription,
            Double height,
            Double width,
            Double length,
            Double weight
    ) {
    }

    record GetImage(
            Long id,
            String name,
            String fileName,
            Integer order
    ) {
    }

}