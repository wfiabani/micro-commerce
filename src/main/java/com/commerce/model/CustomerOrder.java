package com.commerce.model;

import com.commerce.validator.ValidCPF;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "customer_order")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_order_generator")
    @SequenceGenerator(name = "customer_order_generator", sequenceName = "customer_order_seq", allocationSize = 1)
    private Long id;

    @Column(name = "order_identifier", nullable = false, unique = true)
    @NotBlank(message = "Identifier is required")
    private String orderIdentifier;

    @Column(name = "total_value", nullable = false)
    @NotNull(message = "Total value is required")
    private BigDecimal totalValue;

    @Column(name = "total_product_value", nullable = false)
    @NotNull(message = "Total product value is required")
    private BigDecimal totalProductValue;

    @Column(name = "shipping_value", nullable = false)
    @NotNull(message = "Shipping value is required")
    private BigDecimal shippingValue;

    @Column(name = "delivery_time", nullable = false)
    @NotBlank(message = "Delivery time is required")
    private String deliveryTime;

    @Column(name = "shipping_method", nullable = false, length = 50)
    @NotBlank(message = "Shipping method is required")
    private String shippingMethod;

    @Column(name = "full_name", nullable = false, length = 100)
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Column(name = "cpf", nullable = false, length = 11)
    @NotBlank(message = "CPF is required")
    @ValidCPF
    private String cpf;

    @Column(name = "email", nullable = false, length = 100)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Column(name = "delivery_street", nullable = false)
    @NotBlank(message = "Delivery street is required")
    private String deliveryStreet;

    @Column(name = "delivery_number", nullable = false)
    @NotBlank(message = "Delivery number is required")
    private String deliveryNumber;

    @Column(name = "delivery_postal_code", nullable = false, length = 10)
    @NotBlank(message = "Delivery postal code is required")
    @Pattern(regexp = "\\d{8}", message = "Postal code must be in the format 00000000")
    private String deliveryPostalCode;

    @Column(name = "delivery_neighborhood", nullable = false)
    @NotBlank(message = "Delivery neighborhood is required")
    private String deliveryNeighborhood;

    @Column(name = "delivery_city", nullable = false)
    @NotBlank(message = "Delivery city is required")
    private String deliveryCity;

    @Column(name = "delivery_state", nullable = false, length = 2)
    @NotBlank(message = "Delivery state is required")
    private String deliveryState;

    @Column(name = "billing_street", nullable = false)
    @NotBlank(message = "Billing street is required")
    private String billingStreet;

    @Column(name = "billing_number", nullable = false)
    @NotBlank(message = "Billing number is required")
    private String billingNumber;

    @Column(name = "billing_postal_code", nullable = false, length = 10)
    @NotBlank(message = "Billing postal code is required")
    @Pattern(regexp = "\\d{8}", message = "Postal code must be in the format 00000000")
    private String billingPostalCode;

    @Column(name = "billing_neighborhood", nullable = false)
    @NotBlank(message = "Billing neighborhood is required")
    private String billingNeighborhood;

    @Column(name = "billing_city", nullable = false)
    @NotBlank(message = "Billing city is required")
    private String billingCity;

    @Column(name = "billing_state", nullable = false, length = 2)
    @NotBlank(message = "Billing state is required")
    private String billingState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomerOrderItem> items;


    public Long getId() {
        return id;
    }

    public String getOrderIdentifier() {
        return orderIdentifier;
    }

    public void setOrderIdentifier(String orderIdentifier) {
        this.orderIdentifier = orderIdentifier;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigDecimal getTotalProductValue() {
        return totalProductValue;
    }

    public void setTotalProductValue(BigDecimal totalProductValue) {
        this.totalProductValue = totalProductValue;
    }

    public BigDecimal getShippingValue() {
        return shippingValue;
    }

    public void setShippingValue(BigDecimal shippingValue) {
        this.shippingValue = shippingValue;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeliveryStreet() {
        return deliveryStreet;
    }

    public void setDeliveryStreet(String deliveryStreet) {
        this.deliveryStreet = deliveryStreet;
    }

    public String getDeliveryNumber() {
        return deliveryNumber;
    }

    public void setDeliveryNumber(String deliveryNumber) {
        this.deliveryNumber = deliveryNumber;
    }

    public String getDeliveryPostalCode() {
        return deliveryPostalCode;
    }

    public void setDeliveryPostalCode(String deliveryPostalCode) {
        this.deliveryPostalCode = deliveryPostalCode;
    }

    public String getDeliveryNeighborhood() {
        return deliveryNeighborhood;
    }

    public void setDeliveryNeighborhood(String deliveryNeighborhood) {
        this.deliveryNeighborhood = deliveryNeighborhood;
    }

    public String getDeliveryCity() {
        return deliveryCity;
    }

    public void setDeliveryCity(String deliveryCity) {
        this.deliveryCity = deliveryCity;
    }

    public String getDeliveryState() {
        return deliveryState;
    }

    public void setDeliveryState(String deliveryState) {
        this.deliveryState = deliveryState;
    }

    public String getBillingStreet() {
        return billingStreet;
    }

    public void setBillingStreet(String billingStreet) {
        this.billingStreet = billingStreet;
    }

    public String getBillingNumber() {
        return billingNumber;
    }

    public void setBillingNumber(String billingNumber) {
        this.billingNumber = billingNumber;
    }

    public String getBillingPostalCode() {
        return billingPostalCode;
    }

    public void setBillingPostalCode(String billingPostalCode) {
        this.billingPostalCode = billingPostalCode;
    }

    public String getBillingNeighborhood() {
        return billingNeighborhood;
    }

    public void setBillingNeighborhood(String billingNeighborhood) {
        this.billingNeighborhood = billingNeighborhood;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public String getBillingState() {
        return billingState;
    }

    public void setBillingState(String billingState) {
        this.billingState = billingState;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<CustomerOrderItem> getItems() {
        return items;
    }

    public void setItems(List<CustomerOrderItem> items) {
        this.items = items;
    }
}
