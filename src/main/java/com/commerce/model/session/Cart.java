package com.commerce.model.session;

import com.commerce.controller.product.schema.ProductMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;
import java.util.Map;

@Component
@SessionScope
public class Cart {
    private Map<ProductMapper.GetProduct, Integer> items = new HashMap<>();

    public Map<ProductMapper.GetProduct, Integer> getItems() {
        return items;
    }

    public void addItem(ProductMapper.GetProduct product, int quantity) {
        items.put(product, items.getOrDefault(product, 0) + quantity);
    }

    public void removeItem(ProductMapper.GetProduct product) {
        items.remove(product);
    }

    public void clear() {
        items.clear();
    }

    public void updateItemQuantity(ProductMapper.GetProduct product, Integer newQuantity) {
        if (items.containsKey(product)) {
            items.put(product, newQuantity);
        }
    }

}