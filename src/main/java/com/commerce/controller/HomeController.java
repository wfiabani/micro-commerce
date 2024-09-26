package com.commerce.controller;


import com.commerce.model.Product;
import com.commerce.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("message", "Bem-vindo à aplicação Spring Boot com Thymeleaf!");

        logger.info("This is an INFO log message.");
        logger.debug("This is a DEBUG log message.");
        logger.warn("This is a WARN log message.");
        logger.error("This is an ERROR log message.");

        return "home";
    }

    @GetMapping("/1")
    public ResponseEntity<Product> getProductById1() {
        Optional<Product> product = productRepository.findById(1L);

        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
