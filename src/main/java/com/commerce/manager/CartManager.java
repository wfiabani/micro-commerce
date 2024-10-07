package com.commerce.manager;

import com.commerce.controller.product.schema.ProductMapper;
import com.commerce.model.session.Cart;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Service
@Validated
public class CartManager {

    private final Cart cart;

    public CartManager(Cart cart) {
        this.cart = cart;
    }

    public Cart getCart() {
        return cart;
    }

    public void addItem(ProductMapper.GetProduct product, Integer quantity) {
        // Verifica se a quantidade solicitada é maior do que o estoque disponível
        if (quantity > product.stock()) {
            throw new IllegalArgumentException("A quantidade solicitada excede o estoque disponível.");
        }
        cart.addItem(product, quantity);
        invalidateShipping();
        updateTotalValue();
    }

    public void updateItemQuantity(ProductMapper.GetProduct product, Integer newQuantity) {
        if (newQuantity <= 0) {
            removeItem(product);
        } else {
            // Verifica se a quantidade solicitada é maior do que o estoque disponível
            if (newQuantity > product.stock()) {
                throw new IllegalArgumentException("A quantidade solicitada excede o estoque disponível.");
            }
            cart.updateItemQuantity(product, newQuantity);
        }
        invalidateShipping();
        updateTotalValue();
    }

    public void removeItem(ProductMapper.GetProduct product) {
        cart.removeItem(product);
        invalidateShipping();
        updateTotalValue();
    }

    public void clear() {
        cart.clear();
        invalidateShipping();
        updateTotalValue();
    }

    private void invalidateShipping() {
        cart.setShippingValue(BigDecimal.ZERO);
        cart.setShippingValid(false);
        cart.setTotalValue(BigDecimal.ZERO);
        cart.setTotalProductValue(BigDecimal.ZERO);
        cart.setDeliveryTime(null);
    }

    private void updateTotalValue() {
        BigDecimal totalValue = cart.getItems().entrySet().stream()
                .map(entry -> {
                    BigDecimal price = entry.getKey().price();
                    int quantity = entry.getValue();
                    return price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalProductValue(totalValue);
        totalValue = totalValue.add(cart.getShippingValue());
        cart.setTotalValue(totalValue);
    }

    public void calculateShipping(String postalCode) {
        BigDecimal calculatedShippingValue = new BigDecimal("15.00");
        cart.setShippingValue(calculatedShippingValue);
        cart.setDeliveryTime("5 days");
        cart.setShippingValid(true);
        updateTotalValue();
    }
}
