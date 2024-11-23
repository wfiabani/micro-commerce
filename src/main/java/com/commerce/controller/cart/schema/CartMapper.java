package com.commerce.controller.cart.schema;

import com.commerce.controller.product.schema.ProductMapper;
import com.commerce.model.session.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface CartMapper {

    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

    GetCart toGetCart(Cart cart);

    default List<GetCartItem> toGetCartItems(Map<ProductMapper.GetProduct, Integer> items) {
        List<GetCartItem> getCartItems = new ArrayList<>();
        items.forEach((product, quantity) ->
                getCartItems.add(new GetCartItem(product, quantity))
        );
        return getCartItems;
    }

    default GetCartTotalItems toGetCartTotalItems(Cart cart) {
        int totalQuantity = cart.getItems().values().stream().mapToInt(Integer::intValue).sum();
        return new GetCartTotalItems(totalQuantity);
    }

    record GetCart(
            List<GetCartItem> items,
            BigDecimal shippingValue,
            BigDecimal totalProductValue,
            BigDecimal totalValue,
            boolean isShippingValid,
            String deliveryTime,
            String postalCode, // Novo campo para o CEP
            String shippingMethodName // Novo campo para o Nome do método de envio
    ) {
    }

    record GetCartItem(
            ProductMapper.GetProduct product,
            Integer quantity
    ) {
    }

    record PostProductItem(
        Long productId,
        Integer quantity
    ){}

    record GetCartTotalItems(
            Integer totalQuantity
    ) {}
}
