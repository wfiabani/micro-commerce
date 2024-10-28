package com.commerce.model

enum class OrderStatus {
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
