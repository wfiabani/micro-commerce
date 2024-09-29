package com.commerce.controller.product;

import com.commerce.controller.product.schema.ProductMapper;
import com.commerce.manager.ProductManager;
import com.commerce.model.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("produto")
@Validated
public class ProductController {

    private ProductManager productManager;

    public ProductController(ProductManager productManager) {
        this.productManager = productManager;
    }

    @GetMapping("/{id}/{productName:.*}")
    public String getProductById(Model model,
                                 @PathVariable Long id,
                                 @PathVariable(required = false) String productName) {
        Product product = productManager.getProduct(id);
        ProductMapper.GetProduct data = ProductMapper.INSTANCE.toGetProduct(product);
        System.out.println(data);
        model.addAttribute("data", data);
        model.addAttribute("template", "product/product-details");
        return "layout";
    }

}
