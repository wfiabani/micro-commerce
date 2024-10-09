package com.commerce.manager;

import com.commerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class OrderManager {

    private final ProductRepository productRepository;

    public OrderManager(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

}
