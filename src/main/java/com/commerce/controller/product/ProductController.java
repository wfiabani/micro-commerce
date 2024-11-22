package com.commerce.controller.product;

import com.commerce.controller.product.schema.ProductMapper;
import com.commerce.exception.ProductNotFoundException;
import com.commerce.manager.ProductManager;
import com.commerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("product")
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

    @GetMapping("/category/{categoryId}/{categorySlug:.*}")
    public String getProductsByCategory(Model model,
                                        @PathVariable Long categoryId,
                                        @PathVariable String categorySlug,
                                        @RequestParam(defaultValue = "0") int page) {
        int pageSize = 3;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<Product> productsPage = productManager.getProductsByCategory(categoryId, pageRequest);
        Page<ProductMapper.GetProduct> dataPage = ProductMapper.INSTANCE.toGetProductPage(productsPage);
        // Adiciona informações ao modelo para exibição no HTML
        model.addAttribute("data", dataPage.getContent()); // Lista de produtos
        model.addAttribute("currentPage", dataPage.getNumber()); // Página atual (base 0)
        model.addAttribute("totalPages", dataPage.getTotalPages()); // Total de páginas
        model.addAttribute("totalElements", dataPage.getTotalElements()); // Total de itens
        model.addAttribute("pageSize", dataPage.getSize()); // Tamanho da página
        model.addAttribute("categoryId", categoryId); // ID da categoria (para navegação)
        model.addAttribute("categorySlug", categorySlug); // Slug da categoria (para navegação)
        model.addAttribute("template", "product/products-list"); // Template de exibição
        return "layout";
    }

}
