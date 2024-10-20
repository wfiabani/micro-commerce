package com.commerce.manager;

import com.commerce.controller.order.schema.CustomerOrderMapper;
import com.commerce.exception.CustomerOrderNotFoundException;
import com.commerce.exception.OrderProcessingException;
import com.commerce.model.CustomerOrder;
import com.commerce.model.session.Cart;
import com.commerce.repository.CustomerOrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.Random;

@Service
@Validated
public class CustomerOrderManager {

    private final CustomerOrderRepository customerOrderRepository;
    private final Cart cart;

    public CustomerOrderManager(CustomerOrderRepository customerOrderRepository, Cart cart) {
        this.customerOrderRepository = customerOrderRepository;
        this.cart = cart;
    }

    @Transactional
    public CustomerOrder addCustomerOrder(CustomerOrderMapper.PostCustomerOrder postCustomerOrder) {
        try {
            CustomerOrder customerOrder = CustomerOrderMapper.INSTANCE.toCustomerOrder(postCustomerOrder);
            customerOrder.setShippingMethod(cart.getShippingMethodName());
            customerOrder.setShippingValue(cart.getShippingValue());
            customerOrder.setTotalProductValue(cart.getTotalProductValue());
            customerOrder.setTotalValue(cart.getTotalValue());
            customerOrder.setDeliveryTime(cart.getDeliveryTime());
            String orderIdentifier = generateOrderIdentifier();
            customerOrder.setOrderIdentifier(orderIdentifier);
            var savedOrder = customerOrderRepository.save(customerOrder);
            return savedOrder;
        } catch (IllegalArgumentException e) {
            throw new OrderProcessingException("Invalid input data: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new OrderProcessingException("Data integrity violation: " + e.getMessage());
        } catch (TransactionSystemException e) {
            throw new OrderProcessingException("Transaction failed: " + e.getMessage());
        } catch (Exception e) {
            throw new OrderProcessingException("Failed to add customer order: " + e.getMessage());
        }
    }


    public CustomerOrder getOrder(String id) {
        CustomerOrder customerOrder = customerOrderRepository.findByOrderIdentifier(id)
                .orElseThrow(() -> new CustomerOrderNotFoundException("Customer order with identifier " + id + " not found."));
        return customerOrder;
    }

    private String generateOrderIdentifier() {
        long timestamp = Instant.now().toEpochMilli();
        int randomValue = new Random().nextInt(10000);
        return String.format("%d-%04d", timestamp, randomValue);
    }

}
