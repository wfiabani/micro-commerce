package com.commerce.manager;

import com.commerce.exception.ProductNotFoundException;
import com.commerce.model.Product;
import com.commerce.repository.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ProductManager {

    private final ProductRepository productRepository;

    public ProductManager(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable("products")
    public Product getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found."));
        product.getImages();
        product.getCategory();
        return product;
    }

    @Cacheable("products")
    public Page<Product> getProductsByCategory(Long categoryId, PageRequest pageRequest) {
        return productRepository.findAllByCategoryId(categoryId, pageRequest);
    }
}
