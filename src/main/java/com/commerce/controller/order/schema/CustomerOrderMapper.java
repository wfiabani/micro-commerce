package com.commerce.controller.order.schema;

import com.commerce.model.CustomerOrder;
import com.commerce.model.CustomerOrderItem;
import com.commerce.validator.ValidCPF;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CustomerOrderMapper {

    CustomerOrderMapper INSTANCE = Mappers.getMapper(CustomerOrderMapper.class);

    GetCustomerOrder toGetCustomerOrder(CustomerOrder customerOrder);

    CustomerOrder toCustomerOrder(PostCustomerOrder createCustomerOrder);

    List<GetCustomerOrderItem> toGetCustomerOrderItems(List<CustomerOrderItem> items);

    record GetCustomerOrder(
            Long id,
            String orderIdentifier,
            BigDecimal totalValue,
            BigDecimal totalProductValue,
            BigDecimal shippingValue,
            String deliveryTime,
            String shippingMethod,
            String fullName,
            String cpf,
            String email,
            String deliveryStreet,
            String deliveryNumber,
            String deliveryPostalCode,
            String deliveryNeighborhood,
            String deliveryCity,
            String deliveryState,
            String billingStreet,
            String billingNumber,
            String billingPostalCode,
            String billingNeighborhood,
            String billingCity,
            String billingState,
            List<GetCustomerOrderItem> items
    ) {
    }

    record PostCustomerOrder(
            @NotBlank(message = "Full name is required")
            String fullName,

            @NotBlank(message = "CPF is required")
            @ValidCPF
            String cpf,

            @NotBlank(message = "Email is required")
            @Email(message = "Email should be valid")
            String email,

            @NotBlank(message = "Delivery street is required")
            String deliveryStreet,

            @NotBlank(message = "Delivery number is required")
            String deliveryNumber,

            @NotBlank(message = "Delivery postal code is required")
            @Pattern(regexp = "\\d{8}", message = "Postal code must be in the format 00000000")
            String deliveryPostalCode,

            @NotBlank(message = "Delivery neighborhood is required")
            String deliveryNeighborhood,

            @NotBlank(message = "Delivery city is required")
            String deliveryCity,

            @NotBlank(message = "Delivery state is required")
            String deliveryState,

            @NotBlank(message = "Billing street is required")
            String billingStreet,

            @NotBlank(message = "Billing number is required")
            String billingNumber,

            @NotBlank(message = "Billing postal code is required")
            @Pattern(regexp = "\\d{8}", message = "Postal code must be in the format 00000000")
            String billingPostalCode,

            @NotBlank(message = "Billing neighborhood is required")
            String billingNeighborhood,

            @NotBlank(message = "Billing city is required")
            String billingCity,

            @NotBlank(message = "Billing state is required")
            String billingState

    ) {}

    record GetCustomerOrderItem(
            Long id,
            Long productId,
            BigDecimal unitPrice,
            Integer quantity
    ) {}
}
