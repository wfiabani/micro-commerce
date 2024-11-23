package com.commerce.repository;

import com.commerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {

    Page<Product> findAllByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    Page<Product> findAllByIsActiveTrue(Pageable pageable);

}
