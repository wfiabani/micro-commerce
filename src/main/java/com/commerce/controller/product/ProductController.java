package com.commerce.controller.product;

import com.commerce.controller.product.schema.ProductMapper;
import com.commerce.exception.ProductNotFoundException;
import com.commerce.manager.ProductManager;
import com.commerce.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("produto")
@Validated
public class ProductController {

    private final ProductManager productManager;

    public ProductController(ProductManager productManager) {
        this.productManager = productManager;
    }

    @GetMapping("/{id}/{productName:.*}")
    public String getProductById(Model model,
                                 @PathVariable Long id,
                                 @PathVariable(required = false) String productName) {
        try {
            Product product = productManager.getProduct(id);
            ProductMapper.GetProduct data = ProductMapper.INSTANCE.toGetProduct(product);
            model.addAttribute("data", data);
            model.addAttribute("template", "product/product-details");
            return "layout";
        } catch (ProductNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/error"; // Redireciona para uma página de erro customizada
        }
    }

    @GetMapping("/{id}/json")
    @ResponseBody
    public ResponseEntity<ProductMapper.GetProduct> getProductByIdJson(@PathVariable Long id) {
        try {
            Product product = productManager.getProduct(id);
            ProductMapper.GetProduct data = ProductMapper.INSTANCE.toGetProduct(product);
            return ResponseEntity.ok(data);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
}
