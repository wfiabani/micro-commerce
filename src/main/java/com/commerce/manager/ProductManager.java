package com.commerce.manager;

import com.commerce.model.Product;
import com.commerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
public class ProductManager {

    private final ProductRepository productRepository;

    public ProductManager(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProduct(Long id){
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            Product p = product.get();
            p.getImages();
            p.getCategory();
            return p;
        }else{
            throw new RuntimeException();
        }
    }
}
