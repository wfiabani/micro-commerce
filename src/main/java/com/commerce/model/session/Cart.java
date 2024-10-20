package com.commerce.model.session;

import com.commerce.controller.product.schema.ProductMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@SessionScope
public class Cart {
    private Map<ProductMapper.GetProduct, Integer> items = new HashMap<>();
    private BigDecimal shippingValue = BigDecimal.ZERO;
    private BigDecimal totalValue = BigDecimal.ZERO;
    private BigDecimal totalProductValue = BigDecimal.ZERO;
    private boolean isShippingValid = false;
    private String deliveryTime;
    private String postalCode;
    private String shippingMethodName;

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

    public BigDecimal getShippingValue() {
        return shippingValue;
    }

    public void setShippingValue(BigDecimal shippingValue) {
        this.shippingValue = shippingValue;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public boolean getIsShippingValid() {
        return isShippingValid;
    }

    public void setShippingValid(boolean shippingValid) {
        isShippingValid = shippingValid;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public BigDecimal getTotalProductValue() {
        return totalProductValue;
    }

    public void setTotalProductValue(BigDecimal totalProductValue) {
        this.totalProductValue = totalProductValue;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getShippingMethodName() {
        return shippingMethodName;
    }

    public void setShippingMethodName(String shippingMethodName) {
        this.shippingMethodName = shippingMethodName;
    }
}
