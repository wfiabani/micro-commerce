package com.commerce.manager;

import com.commerce.controller.product.schema.ProductMapper;
import com.commerce.exception.InactiveProductException;
import com.commerce.exception.InsufficientStockException;
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
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        if (canAddProductToCart(product, quantity)) {
            cart.addItem(product, quantity);
            invalidateShipping();
            updateTotalValue();
        }
    }

    public void updateItemQuantity(ProductMapper.GetProduct product, Integer newQuantity) {
        if (newQuantity <= 0) {
            removeItem(product);
        } else if (canUpdateProductQuantity(product, newQuantity)) {
            cart.updateItemQuantity(product, newQuantity);
            invalidateShipping();
            updateTotalValue();
        }
    }

    private boolean canAddProductToCart(ProductMapper.GetProduct product, Integer quantity) {
        // Verifica se o produto está inativo
        if (!product.active()) {
            throw new InactiveProductException("Este produto não está disponível para compra.");
        }

        // Calcula a quantidade total do produto já no carrinho
        Integer currentQuantity = cart.getItems().getOrDefault(product, 0);
        Integer totalQuantity = currentQuantity + quantity;

        // Verifica se a quantidade total solicitada é maior ou igual ao estoque disponível
        if (totalQuantity > product.stock()) {
            throw new InsufficientStockException("A quantidade solicitada excede o estoque disponível.");
        }

        return true;
    }

    private boolean canUpdateProductQuantity(ProductMapper.GetProduct product, Integer quantity) {
        // Verifica se o produto está inativo
        if (!product.active()) {
            throw new InactiveProductException("Este produto não está disponível para compra.");
        }

        // Verifica se a quantidade total solicitada é maior ou igual ao estoque disponível
        if (quantity > product.stock()) {
            throw new InsufficientStockException("A quantidade solicitada excede o estoque disponível.");
        }

        return true;
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
        cart.setPostalCode(postalCode);
        cart.setShippingMethodName("PAC - Correios");
        cart.setDeliveryTime("5 dias");
        cart.setShippingValid(true);
        updateTotalValue();
    }
}
