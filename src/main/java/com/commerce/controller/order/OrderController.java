package com.commerce.controller.order;

import com.commerce.exception.ProductNotFoundException;
import com.commerce.manager.ProductManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("pedido")
@Validated
public class OrderController {

    private final ProductManager productManager;

    public OrderController(ProductManager productManager) {
        this.productManager = productManager;
    }

    @GetMapping("/detalhes-do-pedido")
    public String getOrderForm(Model model) {
        try {
            model.addAttribute("template", "order/order-details");
            return "layout";
        } catch (ProductNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/error"; // Redireciona para uma página de erro customizada
        }
    }


}
