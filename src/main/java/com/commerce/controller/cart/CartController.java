package com.commerce.controller.cart;

import com.commerce.controller.cart.schema.CartMapper;
import com.commerce.controller.product.schema.ProductMapper;
import com.commerce.manager.CartManager;
import com.commerce.manager.ProductManager;
import com.commerce.model.session.Cart;
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
        if (cart == null) {
            return ResponseEntity.badRequest().body(null);
        }
        var response = CartMapper.INSTANCE.toGetCart(cart);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<String> addProductToCart(@RequestBody CartMapper.PostProductItem request) {
        var product = productManager.getProduct(request.productId());
        if (product == null) {
            return ResponseEntity.badRequest().body("Product not found.");
        }
        cartManager.addItem(ProductMapper.INSTANCE.toGetProduct(product), request.quantity());
        return ResponseEntity.ok("Product added to cart successfully.");
    }

    @PutMapping("/update/{productId}")
    @ResponseBody
    public ResponseEntity<String> updateProductQuantity(@PathVariable Long productId, @RequestParam int newQuantity) {
        var product = productManager.getProduct(productId);
        if (product == null) {
            return ResponseEntity.badRequest().body("Product not found.");
        }
        cartManager.updateItemQuantity(ProductMapper.INSTANCE.toGetProduct(product), newQuantity);
        return ResponseEntity.ok("Product quantity updated successfully.");
    }

    @DeleteMapping("/remove/{productId}")
    @ResponseBody
    public ResponseEntity<String> removeProductFromCart(@PathVariable Long productId) {
        var product = productManager.getProduct(productId);
        if (product == null) {
            return ResponseEntity.badRequest().body("Product not found.");
        }
        cartManager.removeItem(ProductMapper.INSTANCE.toGetProduct(product));
        return ResponseEntity.ok("Product removed from cart successfully.");
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
