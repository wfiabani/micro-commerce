package com.commerce.repository;

import com.commerce.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CustomerOrderItemRepository extends JpaRepository<CustomerOrder, Long>, PagingAndSortingRepository<CustomerOrder, Long> {

    Optional<CustomerOrder> findByOrderIdentifier(String orderIdentifier);

}