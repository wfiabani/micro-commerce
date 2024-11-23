package com.commerce.controller.home;


import com.commerce.controller.product.schema.ProductMapper;
import com.commerce.manager.ProductManager;
import com.commerce.model.Product;
import com.commerce.util.TemplateConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final ProductManager productManager;

    public HomeController(ProductManager productManager) {
        this.productManager = productManager;
    }

    @GetMapping({"/home", "/"})
    public String getProducts(Model model,
                              @RequestParam(defaultValue = "0") int page) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<Product> productsPage = productManager.getProducts(pageRequest);
        Page<ProductMapper.GetProduct> dataPage = ProductMapper.INSTANCE.toGetProductPage(productsPage);
        model.addAttribute("data", dataPage.getContent());
        model.addAttribute("currentPage", dataPage.getNumber());
        model.addAttribute("totalPages", dataPage.getTotalPages());
        model.addAttribute("totalElements", dataPage.getTotalElements());
        model.addAttribute("pageSize", dataPage.getSize());
        model.addAttribute("template", TemplateConstants.HOME);
        return TemplateConstants.LAYOUT;
    }


}
