package com.commerce.manager;

import com.commerce.controller.order.schema.CustomerOrderMapper;
import com.commerce.controller.product.schema.ProductMapper;
import com.commerce.exception.CustomerOrderNotFoundException;
import com.commerce.exception.OrderProcessingException;
import com.commerce.exception.ProductNotFoundException;
import com.commerce.model.CustomerOrder;
import com.commerce.model.CustomerOrderItem;
import com.commerce.model.OrderStatus;
import com.commerce.model.Product;
import com.commerce.model.session.Cart;
import com.commerce.repository.CustomerOrderRepository;
import com.commerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@Validated
public class CustomerOrderManager {

    private final CustomerOrderRepository customerOrderRepository;
    private final ProductRepository productRepository;
    private final Cart cart;

    public CustomerOrderManager(CustomerOrderRepository customerOrderRepository, ProductRepository productRepository, Cart cart) {
        this.customerOrderRepository = customerOrderRepository;
        this.productRepository = productRepository;
        this.cart = cart;
    }


    @Transactional
    public CustomerOrder addCustomerOrder(CustomerOrderMapper.PostCustomerOrder postCustomerOrder) {
        try {
            CustomerOrder customerOrder = createCustomerOrder(postCustomerOrder);
            List<CustomerOrderItem> orderItems = createOrderItems(customerOrder);
            customerOrder.setItems(orderItems);
            return customerOrderRepository.save(customerOrder);
        } catch (IllegalArgumentException e) {
            throw new OrderProcessingException("Invalid input data: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new OrderProcessingException("Data integrity violation: " + e.getMessage());
        } catch (TransactionSystemException e) {
            throw new OrderProcessingException("Transaction failed: " + e.getMessage());
        } catch (ProductNotFoundException e) {
            throw new OrderProcessingException("Product not found: " + e.getMessage());
        } catch (Exception e) {
            throw new OrderProcessingException("Failed to add customer order: " + e.getMessage());
        }
    }

    public CustomerOrder updateMerchantOrderId(String external_reference, String merchant_order_id) {
        try {
            CustomerOrder customerOrder = getOrder(external_reference);
            customerOrder.setMerchantOrderId(merchant_order_id);
            return customerOrderRepository.save(customerOrder);
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


    private CustomerOrder createCustomerOrder(CustomerOrderMapper.PostCustomerOrder postCustomerOrder) {
        CustomerOrder customerOrder = CustomerOrderMapper.INSTANCE.toCustomerOrder(postCustomerOrder);
        customerOrder.setShippingMethod(cart.getShippingMethodName());
        customerOrder.setShippingValue(cart.getShippingValue());
        customerOrder.setTotalProductValue(cart.getTotalProductValue());
        customerOrder.setTotalValue(cart.getTotalValue());
        customerOrder.setDeliveryTime(cart.getDeliveryTime());
        customerOrder.setStatus(OrderStatus.CREATED);
        String orderIdentifier = generateOrderIdentifier();
        customerOrder.setOrderIdentifier(orderIdentifier);
        return customerOrder;
    }

    private List<CustomerOrderItem> createOrderItems(CustomerOrder customerOrder) {
        List<CustomerOrderItem> orderItems = new ArrayList<>();
        Map<ProductMapper.GetProduct, Integer> items = cart.getItems();
        for (Map.Entry<ProductMapper.GetProduct, Integer> entry : items.entrySet()) {
            Integer quantity = entry.getValue();
            Product product = productRepository.findById(entry.getKey().id())
                    .orElseThrow(() -> new ProductNotFoundException("Product with ID " + entry.getKey().id() + " not found."));

            CustomerOrderItem orderItem = new CustomerOrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(entry.getKey().price());
            orderItem.setCustomerOrder(customerOrder);
            orderItems.add(orderItem);
        }
        return orderItems;
    }


    public CustomerOrder getOrder(String id) {
        CustomerOrder customerOrder = customerOrderRepository.findByOrderIdentifier(id)
                .orElseThrow(() -> new CustomerOrderNotFoundException("Customer order with identifier " + id + " not found."));
        return customerOrder;
    }

    public CustomerOrder getOrderByMerchantOrderId(String merchantOrderId) {
        CustomerOrder customerOrder = customerOrderRepository.findByMerchantOrderId(merchantOrderId)
                .orElseThrow(() -> new CustomerOrderNotFoundException("Customer order with merchant order id " + merchantOrderId + " not found."));
        return customerOrder;
    }

    private String generateOrderIdentifier() {
        long timestamp = Instant.now().toEpochMilli();
        int randomValue = new Random().nextInt(10000);
        return String.format("%d-%04d", timestamp, randomValue);
    }

}
