package com.commerce.controller.product.schema;

import com.commerce.model.Image;
import com.commerce.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductMapper {

    public ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "images", target = "images")
    ProductMapper.GetProduct toGetProduct(Product product);

    ImageDTO toImageDTO(Image image);

    record GetProduct(
            Long id,
            String name,
            String categoryName,
            List<ImageDTO> images, // Lista de imagens detalhadas
            String shortDescription,
            String longDescription,
            Integer stock,
            String productId,
            Double price
    ) {
    }

    record ImageDTO(
            Long id,
            String name,
            String fileName,
            Integer order
    ) {}


}