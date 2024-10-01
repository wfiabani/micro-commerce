package com.commerce.manager;

import com.commerce.controller.product.schema.ProductMapper;
import com.commerce.model.session.Cart;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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

    public void addItem(ProductMapper.GetProduct product, Integer quantity){
        cart.addItem(product, quantity);
    }

    public void updateItemQuantity(ProductMapper.GetProduct product, Integer newQuantity) {
        if (newQuantity <= 0) {
            removeItem(product);
        } else {
            cart.updateItemQuantity(product, newQuantity);
        }
    }

    public void removeItem(ProductMapper.GetProduct product) {
        cart.removeItem(product);
    }

    public void clear() {
        cart.clear();
    }
}
