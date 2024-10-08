package com.commerce.controller.cart;

import com.commerce.controller.cart.schema.CartMapper;
import com.commerce.controller.product.schema.ProductMapper;
import com.commerce.exception.InactiveProductException;
import com.commerce.exception.InsufficientStockException;
import com.commerce.exception.ProductNotFoundException;
import com.commerce.manager.CartManager;
import com.commerce.manager.ProductManager;
import com.commerce.model.session.Cart;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("carrinho-de-compras")
@Validated
public class CartController {

    private CartManager cartManager;
    private ProductManager productManager;

    public CartController(CartManager cartManager, ProductManager productManager) {
        this.cartManager = cartManager;
        this.productManager = productManager;
    }

    @GetMapping("/resumo")
    public String getCart(Model model) {
        Cart cart = cartManager.getCart();
        model.addAttribute("cart", CartMapper.INSTANCE.toGetCart(cart));
        model.addAttribute("template", "cart/cart");
        return "layout";
    }

    @GetMapping("/get-items")
    @ResponseBody
    public ResponseEntity<CartMapper.GetCart> getJSONCart() {
        Cart cart = cartManager.getCart();
        var response = CartMapper.INSTANCE.toGetCart(cart);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<String> addProductToCart(@RequestBody CartMapper.PostProductItem request) {
        try {
            var product = productManager.getProduct(request.productId());
            cartManager.addItem(ProductMapper.INSTANCE.toGetProduct(product), request.quantity());
            return ResponseEntity.ok("Product added to cart successfully.");
        } catch (InactiveProductException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/update/{productId}")
    @ResponseBody
    public ResponseEntity<String> updateProductQuantity(@PathVariable Long productId, @RequestParam int newQuantity) {
        try {
            var product = productManager.getProduct(productId);
            cartManager.updateItemQuantity(ProductMapper.INSTANCE.toGetProduct(product), newQuantity);
            return ResponseEntity.ok("Product quantity updated successfully.");
        } catch (InactiveProductException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/remove/{productId}")
    @ResponseBody
    public ResponseEntity<String> removeProductFromCart(@PathVariable Long productId) {
        try {
            var product = productManager.getProduct(productId);
            cartManager.removeItem(ProductMapper.INSTANCE.toGetProduct(product));
            return ResponseEntity.ok("Product removed from cart successfully.");
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    @ResponseBody
    public ResponseEntity<String> clear() {
        cartManager.clear();
        return ResponseEntity.ok("Cart cleared successfully.");
    }

    @PostMapping("/calculate-shipping")
    @ResponseBody
    public ResponseEntity<String> calculateShipping(@RequestParam String cep) {
        try {
            cartManager.calculateShipping(cep);
            return ResponseEntity.ok("Shipping calculated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to calculate shipping: " + e.getMessage());
        }
    }
}
