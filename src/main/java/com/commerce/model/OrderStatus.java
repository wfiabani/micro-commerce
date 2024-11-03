package com.commerce.model;

public enum OrderStatus {
    CREATED,

    // Pedido criado
    PAID,

    // Pedido pago
    SHIPPED,

    // Pedido enviado
    COMPLETED,

    // Pedido concluído
    CANCELLED // Pedido cancelado
}
