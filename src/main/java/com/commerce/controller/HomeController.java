package com.commerce.controller;


import com.commerce.model.Category;
import com.commerce.model.Product;
import com.commerce.repository.ProductRepository;
import com.commerce.manager.CategoryService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/layout")
    public String home(Model model) {
        model.addAttribute("message", "Bem-vindo à aplicação Spring Boot com Thymeleaf!");

        logger.info("This is an INFO log message.");
        logger.debug("This is a DEBUG log message.");
        logger.warn("This is a WARN log message.");
        logger.error("This is an ERROR log message.");

        model.addAttribute("template", "home/home");
        model.addAttribute("variable", "var");

        return "layout";
    }

    @GetMapping("/1")
    @Transactional // Adicione a anotação para garantir que a sessão esteja aberta
    public ResponseEntity<Product> getProductById1() {
        Optional<Product> product = productRepository.findById(1L);

        if (product.isPresent()) {
            // Acesse as imagens para forçar o carregamento
            product.get().getImages().size(); // Força o carregamento
            System.out.println(product.get().getImages());
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/meus-objetos")
    public String listarObjetos(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "2") int size) {
        Page<Category> categorias = categoryService.listar(page, size);
        model.addAttribute("categorias", categorias);
        return "meus-objetos"; // Este deve corresponder ao nome do arquivo HTML
    }


}
