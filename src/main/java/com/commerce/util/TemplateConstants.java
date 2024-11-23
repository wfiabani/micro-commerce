package com.commerce.util;

public class TemplateConstants {
    public static final String LAYOUT = "layout";
    public static final String HEADER = "header";
    public static final String FOOTER = "footer";
    public static final String ERROR = "error/error";
    public static final String PRODUCT_LIST = "product/product-list";
    public static final String PRODUCT_DETAILS = "product/product-details";
    public static final String PRODUCT_CAR = "product/product-card";
    public static final String PRODUCT_PAGINATION = "product/pagination";
    public static final String CART_SUMMARY = "order/cart-summary";
    public static final String ORDER_DETAILS = "order/order-details";
    public static final String HOME = "home/home";
    public static final String HOME_PAGINATION = "home/pagination";
    public static final String CHECKOUT_PAYMENT = "checkout/payment";
    public static final String CHECKOUT_BACKURL_SUCCESS = "checkout/back-urls/success";
    public static final String CHECKOUT_BACKURL_FAILURE = "checkout/back-urls/failure";
    public static final String CHECKOUT_BACKURL_PENDING = "checkout/back-urls/pending";
    public static final String CART = "cart/cart";

    private TemplateConstants() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada");
    }
}
